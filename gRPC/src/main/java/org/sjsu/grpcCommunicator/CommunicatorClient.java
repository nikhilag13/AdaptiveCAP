/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sjsu.grpcCommunicator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.*;
import java.lang.*;

/**
 * A simple client that requests a greeting from the {@link CommunicatorServer}.
 */
public class CommunicatorClient {
  private static final Logger logger = Logger.getLogger(CommunicatorClient.class.getName());

  private ManagedChannel channel;
  private CommunicatorGrpc.CommunicatorBlockingStub blockingStub;

  MongoClient mongoClient = new MongoClient("localhost", 27017);
  DB database = mongoClient.getDB("cmpe295Project");

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
//  public CommunicatorClient(String host, int port) {
//    channel = ManagedChannelBuilder.forAddress(host, port)
//        .usePlaintext(true)
//        .build();
//    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
//  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }



  /** Say hello to server. */
//  public void getNodeSize(int size, String id) {
//    try {
//      logger.info("size is : " + size + " node is : "+ id);
//      MySize request = MySize.newBuilder().setSize(size).build();
//      AccomodateChild response = blockingStub.size(request);
//      logger.info("Greeting: " + response.getMessage());
//    } catch (RuntimeException e) {
//      logger.log(Level.WARNING, "RPC failed", e);
//      return;
//    }
//  }

  /** Start stage one clustering. **/
  public void startStageOneCluster(Node node, String ipAddress) {
    logger.info("startStageOneCluster in CommunicatorClient - printing ipAddress " +ipAddress);
    String[] strArr = ipAddress.split(":");
    String host = strArr[0];
    logger.info("printing host " +host);
    int port = Integer.valueOf(strArr[1]);
    logger.info("printing port after splitting " +port);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    logger.info("printing channel "+channel);
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
    logger.info("printing blockingStub "+blockingStub);
    //CommunicatorClient(nid, port);//port num?
    try {
      logger.info("before calling sendSize in client and print node " +node+ " printing stub " +blockingStub );
      send_Size(node, blockingStub);
      logger.info("after sendSize in client");

    } catch (RuntimeException e) {
      logger.error("Node:{} - {}".format(node.getId(), e));
      logger.error("Printing node error " +node.getId(),e);
      logger.error(e);

    } finally {

      channel.shutdown();
//      blockingStub = "None";
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }

  public void propogate_Cluster_head_Info(Node node, String cluster_Name, int hop_count){
    for(String child : node.getChild_list_Id()) {
      String child_IP = node.getIPfromId(child);
      String[] strArr = child_IP.split(":");
      String host = strArr[0];
      //logger.info("printing host " +host);
      int port = Integer.valueOf(strArr[1]);
      //logger.info("printing port after splitting " +port);

      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      //logger.info("printing channel "+channel);
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);
      //logger.info("printing blockingStub "+blockingStub);
      try {
        logger.info("Node: %s - Sending propagate cluster message to child id: %s " + node.getId() + " " + child);

        JoinClusterRequest request = JoinClusterRequest.newBuilder().setClusterHeadName(cluster_Name).setHopcount(hop_count).build();
        JoinClusterResponse response = blockingStub.joinCluster(request);
        String clusterRPC = response.getJoinClusterResponse();

        System.out.println("Node: {} - Got Response: {} after sending cluster message to child id: {} " + node.getId() + " " + clusterRPC + " " + child);
        logger.info("Node: {} - Got Response: {} after sending cluster message to child id: {} " + node.getId() + " " + clusterRPC + " " + child);

      } catch (RuntimeException e) {
        logger.error("Node:{} - {} " + node.getId());
        logger.error(e);
        //logger.error(traceback.format_exc())
      } finally {
        channel.shutdown();
//      blockingStub = "None";
//      channel = "None";
        Runtime.getRuntime().gc();
      }
    }
  }

  public void send_Size(Node node, CommunicatorGrpc.CommunicatorBlockingStub blockingStub ) {

    System.out.println(node.getSize());
    logger.info("Inside sendSize in client " +node.getSize());
    logger.info("Node: %s - Starting function sendSize" +(node.getId()));
    MySize request = MySize.newBuilder().setSize(node.getSize()).setNodeId(node.getId()).build();
    //MySize request = MySize.newBuilder().setSize(node.getSize()).build();
    AccomodateChild response = blockingStub.size(request);
    logger.info("printing response "+response);
    String sizeRPC = response.getMessage();
    logger.info("printing sizeRPC " +sizeRPC );
    logger.info("Node:" + node.getId() + " - Successfully sent the size message of size" + node.getSize() + " to parentId:" + node.getParent_Id());
    logger.info("Node:" + node.getParent_Id() + "- Responded to Size RPC with reply:" + sizeRPC);

    DBCollection collection = database.getCollection("spanningtree");

    BasicDBObject query = new BasicDBObject();
    query.put("node_id", node.getId());

    if (sizeRPC.equals("Prune")) {
      logger.info("Node:" + node.getId() + " - Got Prune");
      // Become a clusterhead and send Cluster RPC to children
      node.setCluster_head_Id(node.getId());
      node.setParent_Id(null);
      // Set I am the cluster
      node.setIs_Cluster_head(1);
      node.setState("free");
      try {

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("is_Cluster_head", node.getIs_Cluster_head());
        newDocument.put("parent_Id", node.getParent_Id());
        newDocument.put("cluster_head_Id", node.getCluster_head_Id());
        newDocument.put("hop_count", node.getHop_count());
        newDocument.put("size", node.getSize());
        newDocument.put("state", node.getState());
        logger.info("printing schema while updating db " +node.getState()+" "+String.valueOf(node.getIs_Cluster_head()) );


        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        collection.update(query, updateObject);

      } catch (RuntimeException e) {
        logger.error("Node:" + node.getId() + "- not able to update db");
        logger.error(e);
      }
      send_Cluster(node);
    }
    else {
      logger.info("Node:" + node.getId() + "Didn't get Prune");
      //Do nothing if the child is accepted into the current cluster
      //Might need to add cluster ID to the central lookup #Later
    }
  }

  /** Phase2 **/
  public void send_Jam_Signal(List<String> child_Ip_List, String cluster_Head_Id ) {
    for(String ip : child_Ip_List) {
      String[] strArr = ip.split(":");
      String host = strArr[0];
      //logger.info("printing host " +host);
      int port = Integer.valueOf(strArr[1]);
      //logger.info("printing port after splitting " +port);

      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      //logger.info("printing channel "+channel);
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);
      try {
        logger.info("Node: %s - Sending jam to childIp: %s" +cluster_Head_Id+ " " +ip);

        JamRequest request = JamRequest.newBuilder().setNodeId(cluster_Head_Id).build();
        JamResponse response = blockingStub.jam(request);
        String clusterRPC = response.getJamResponse();
        logger.info("Node: {} - Clusterhead got response {} after sending jam to child ip: {} " +cluster_Head_Id+" "+clusterRPC+" "+ip);
      }
      catch(RuntimeException e) {
        logger.error("Node:{} - {} " +cluster_Head_Id);
        logger.error(e);
      }
      finally {
        channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
        Runtime.getRuntime().gc();
      }
    }
  }

  public void send_Cluster(Node node){

    int hopCount = 1;
    if (node.getChild_list_Id() == null) {

      System.out.println("Node: %s - I am clusterhead with no children" + node.getId());
      logger.info("Node: %s - I am clusterhead with no children" + node.getId());
      return;
    }
    List<String> childList = node.getChild_list_Id();

    for (String child : childList) {
      String childIP = node.getIPfromId(child);
      String[] strArr = childIP.split(":");
      String host = strArr[0];
      int port = Integer.valueOf(strArr[1]);
      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);

      try {
          logger.info("Node: %s - Sending cluster message to child id: %s" + childIP);
        JoinClusterRequest request = JoinClusterRequest.newBuilder().setHopcount(hopCount).setClusterHeadName(node.getId()).build();
        JoinClusterResponse  response =  blockingStub.joinCluster(request);
        String clusterRPC = response.getJoinClusterResponse();

      } catch (RuntimeException e) {
        logger.error("Node:{} - {}" + node.getId());

      } finally {
        channel.shutdown();
        Runtime.getRuntime().gc();
      }
    }
  }

  public void propagateNewClusterHeadToChildren(List<String> childIpList, String Id, String cluster_head_Id) {

    // have to check corresponding call in Node later

    for (String childIp : childIpList) {
      String[] strArr = childIp.split(":");
      String host = strArr[0];
      int port = Integer.valueOf(strArr[1]);
      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);

      try {

        UpdateClusterheadRequest request = UpdateClusterheadRequest.newBuilder().setNewClusterheadId(cluster_head_Id).build();
        UpdateClusterheadResponse response = blockingStub.updateClusterhead(request);
        logger.info("Node: %s - Sent sendShiftNodeRequest about C:%s to clusterhead: " + Id + " " + cluster_head_Id);

      } catch (RuntimeException e) {
        logger.error("Error with Node  " + childIp);
        logger.error(e);
      } finally {
        channel.shutdown();
        Runtime.getRuntime().gc();
      }

    }

  }

  /** Phase2 **/
  public void send_Shift_Node_Request(Node node, String best_Node_Cluster_Head_Id, String cluster_Head_Ip){

      String[] strArr = cluster_Head_Ip.split(":");
      String host = strArr[0];
      int port = Integer.valueOf(strArr[1]);

      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);
      try {
        ShiftRequest request = ShiftRequest.newBuilder().setNodeId(node.getId()).setSumOfweight(node.getSize()).setClusterHeadId(best_Node_Cluster_Head_Id).build();
        ShiftResponse response =  blockingStub.shiftNodeRequest(request);
        String clusterRPC = response.getMessage();
        logger.info("Node: %s - Sent sendShiftNodeRequest about C:%s to clusterhead: " +node.getId()+ " " +best_Node_Cluster_Head_Id+ " " +cluster_Head_Ip);
      }
      catch(RuntimeException e) {
        logger.error("Node:{} - {} " +node.getId());
        logger.error(e);
      }
      finally {
        channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
        Runtime.getRuntime().gc();
      }
  }


  /** Phase2 **/
  public void send_Shift_Cluster_Request(String cluster_head_Id, String shift_Node_Id, int shift_Node_Sum, String shift_Node_Cluster_Ip) {
    // Sending shift cluster request to cluster head Cj
    String[] strArr = shift_Node_Cluster_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);

    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {
      logger.info("Node: %s - Clusterhead sending ShiftClusterRequest to ClusterheadIp: " +cluster_head_Id+ " " +shift_Node_Cluster_Ip);

      ShiftClusterReq request = ShiftClusterReq.newBuilder().setSenderClusterHeadId(cluster_head_Id).setSenderNodeId(shift_Node_Id).setSumOfweights(shift_Node_Sum).build();
      ShiftClusterRes response = blockingStub.shiftClusterRequest(request);
      String clusterRPC = response.getMessage();

      logger.info("Node: {} - Got Response: {} after sending shift cluster request to Node ip: {} " +cluster_head_Id+ "," +clusterRPC+ "," +shift_Node_Cluster_Ip);
    }
    catch(RuntimeException e) {
      logger.error("Node:{} - {} " +cluster_head_Id);
      logger.error(e);
    }
    finally {
      channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }

  /** Phase2 **/
  public void send_Accept(String cluster_Head_Id, String sender_Cluster_Head_Ip) {

    String[] strArr = sender_Cluster_Head_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);

    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {
      logger.info("Node:%s  - Sending shift Accept to Node ip: %s " +cluster_Head_Id+ "," +sender_Cluster_Head_Ip);

      AcceptRequest request = AcceptRequest.newBuilder().setClusterHeadId(cluster_Head_Id).build();
      AcceptResponse response = blockingStub.accept(request);
      String clusterRPC = response.getMessage();

      logger.info("Node: {} - Got Response: {} after sending shift Accept to Node ip: {} " +cluster_Head_Id+ "," +clusterRPC+ "," +sender_Cluster_Head_Ip);
    }
    catch(RuntimeException e) {
      logger.error("Node:{} - {} " +cluster_Head_Id);
      logger.error(e);
    }
    finally {
      channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }

  /** Phase2 **/
  public void send_Reject(String cluster_Head_Id, String sender_Cluster_Head_Ip){

    String[] strArr = sender_Cluster_Head_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);

    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {
      RejectRequest request = RejectRequest.newBuilder().setClusterHeadId(cluster_Head_Id).build();
      RejectResponse response = blockingStub.reject(request);
      String clusterRPC = response.getMessage();
      logger.info("Node: %s - Clusterhead sent shift reject to Node ip: %s " +cluster_Head_Id+ "," +sender_Cluster_Head_Ip);
      logger.info(clusterRPC);
    }
    catch(RuntimeException e) {
      logger.error("Node:{} - {} " +cluster_Head_Id);
      logger.error(e);
    }
    finally {
      channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }

  /** Phase2 **/
  public void propagate_Jam_To_Children(List<String> child_Ip_List, String jam_Id, String node_Id) {

    for(String childIP: child_Ip_List) {
      String[] strArr = childIP.split(":");
      String host = strArr[0];
      int port = Integer.valueOf(strArr[1]);
      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);
      try {
        JamRequest request = JamRequest.newBuilder().setNodeId(jam_Id).build();
        JamResponse response = blockingStub.jam(request);
        String clusterRPC = response.getJamResponse();
        logger.info("Node: {} - Got Response: {} after sending JAM to child ip: {} " + node_Id + "," + clusterRPC + "," + childIP);

      } catch (RuntimeException e) {
        logger.error("Node:{} - {} " +node_Id);
        logger.error(e);
      } finally {
        channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
        Runtime.getRuntime().gc();
      }
    }
  }

  /** Phase2 **/
  public void propagate_WakeUp(List<String> child_Ip_List, String node_Id) {

    for(String childIP : child_Ip_List) {
      String[] strArr = childIP.split(":");
      String host = strArr[0];
      int port = Integer.valueOf(strArr[1]);
      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);
      try {
        wakeUpRequest request = wakeUpRequest.newBuilder().setWakeywakey("wakeup").build();
        wakeUpResponse response = blockingStub.wakeUp(request);
        String clusterRPC = response.getWokenUp();
        logger.info("Node: {} - Got Response: {} after sending wake to child ip: {} " +node_Id+ "," +clusterRPC+ "," +childIP);

      } catch (RuntimeException e) {
        logger.error("Node:{} - {} " +node_Id);
        logger.error(e);
      } finally {
        channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
        Runtime.getRuntime().gc();
      }
    }
  }

  /** Phase2 **/
  public void join_New_Parent(String node_Id, int node_Size, String new_Parent_Ip) {

    String[] strArr = new_Parent_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);

    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {

      JoinNewParentRequest request = JoinNewParentRequest.newBuilder().setChildSize(node_Size).setNodeId(node_Id).build();
      JoinNewParentResponse response = blockingStub.joinNewParent(request);
      String clusterRPC = response.getJoinResponse();
      logger.info("Node: {} - Got Response: {} after sending join request to new parent ip: {} " +node_Id+ "," +clusterRPC+ "," +new_Parent_Ip);
    }
    catch(RuntimeException e) {
      logger.error("Node:{} - {} " +node_Id);
      logger.error(e);
    }
    finally {
      channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }

  /** Phase2 **/
  public void inform_Parent_About_New_Size(int size_Increment, String node_Id, String parent_Ip){

    String[] strArr = parent_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);

    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {

      UpdateSizeRequest request = UpdateSizeRequest.newBuilder().setSizeIncrement(size_Increment).build();
      UpdateSizeResponse response = blockingStub.updateSize(request);
      String clusterRPC = response.getUpdateSizeResponse();
      logger.info("Node: {} - Got Response: {} after sending updateSize request to existing parent ip: {} " +node_Id+ "," +clusterRPC+ "," +parent_Ip);

    }
    catch(RuntimeException e) {
      logger.error("Node:{} - {} " +node_Id);
      logger.error(e);
    }
    finally {
      channel.shutdown();
//      blockingStub = "None"; //Commented Check later
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }

  public void sendShiftCompleteToBothClusterHeads(String old_Cluster_headIp, String new_Cluster_headIp, String node_Id) {

    String[] strArr = old_Cluster_headIp.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {

      SendShiftCompleteAck request = SendShiftCompleteAck.newBuilder().setId(node_Id).setSendShiftCompleteAck("Departed").build();
      ClusterheadAckSendShift response = blockingStub.shiftComplete(request);
      logger.info("Node: Got Response: {} after sending shiftComplete to old clusterhead ip: {} " + old_Cluster_headIp + " " + "Departed");


    } catch (RuntimeException e) {
      logger.error("Error with Node  " + old_Cluster_headIp);
      logger.error(e);
    } finally {
      channel.shutdown();
      Runtime.getRuntime().gc();
    }

    String[] strArr1 = new_Cluster_headIp.split(":");
    String host1 = strArr[0];
    int port1 = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host1, port1)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);

    try {
      SendShiftCompleteAck request = SendShiftCompleteAck.newBuilder().setId(node_Id).setSendShiftCompleteAck("Added").build();
      ClusterheadAckSendShift response = blockingStub.shiftComplete(request);

      logger.info("Node: %s - Sent sendShiftNodeRequest about to clusterhead: " + new_Cluster_headIp + " " + "Added");
    } catch (RuntimeException e) {
      logger.error("Error with Node  " + new_Cluster_headIp);
      logger.error(e);
    } finally {
      channel.shutdown();
      Runtime.getRuntime().gc();
    }
  }


  public void removeChildIdFromParent(String node_Id, String parent_Ip) {
    String[] strArr = parent_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
    try {

      RemoveChildIdFromParentRequest request = RemoveChildIdFromParentRequest.newBuilder().setDepartingChildId(node_Id).build();
      RemoveChildIdFromParentResponse response = blockingStub.removeChildIdFromParent(request);

      logger.info("Node: %s - Got Response: {} after sending removeChildIdFromParent to (old) parent ip: {} " + parent_Ip);
    } catch (RuntimeException e) {
      logger.error("Error with Node  " + node_Id);
      logger.error(e);
    } finally {
      channel.shutdown();
      Runtime.getRuntime().gc();
    }

  }

  public void sendShiftStart(String node_Id,String target_Node_Id,String  target_Node_Ip) {
    String[] strArr = target_Node_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
    try {


      ShiftStartRequest request = ShiftStartRequest.newBuilder().setTargetNodeId(target_Node_Id).build();
      ShiftStartResponse response = blockingStub.shiftStart(request);
      logger.info("Node: {} - Got Response: {} after sending shift start to Node id: {}"+node_Id+"  "+target_Node_Id);
    }catch (RuntimeException e) {
      logger.error("Error with Node  " + node_Id);
      logger.error(e);
    } finally {
      channel.shutdown();
      Runtime.getRuntime().gc();
    }
  }

  public void sendShiftFinished(String node_Id,String target_Node_Ip){
    String[] strArr = target_Node_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
          try {
            ShiftFinishedRequest request = ShiftFinishedRequest.newBuilder().setClusterHeadId(node_Id).build();
            ShiftFinishedResponse response = blockingStub.shiftFinished(request);
            logger.info("Node: {} - Got Response: {} after sending ShiftFinished to Node ip: {}"+node_Id+"  "+target_Node_Ip);
          }catch (RuntimeException e) {
            logger.error("Error with Node  " + node_Id);
            logger.error(e);
          } finally {
            channel.shutdown();
            Runtime.getRuntime().gc();
          }
}

  public void sendWakeUp(List<String> ipList,String node_Id) {
    for(String childIp :ipList) {

     String[] strArr = childIp.split(":");
      String host = strArr[0];
      int port = Integer.valueOf(strArr[1]);
      channel = ManagedChannelBuilder.forAddress(host, port)
              .usePlaintext(true)
              .build();
      blockingStub = CommunicatorGrpc.newBlockingStub(channel);
      try {
        logger.info("Node: %s - Sending wakeup to child ip: %s" +childIp);
        wakeUpRequest request = wakeUpRequest.newBuilder().setWakeywakey(node_Id).build();
        wakeUpResponse response = blockingStub.wakeUp(request);
        logger.info("Node: {} - Got Response: {} after sending wakeup to child ip: {}"+childIp);
      }catch (RuntimeException e) {
        logger.error("Error with Node  " + node_Id);
        logger.error(e);
      } finally {
        channel.shutdown();
        Runtime.getRuntime().gc();
      }
    }
  }


  public void sendHello(String node_Id,String id,String neighbour_Ip,String node_Cluster_head_Id,int node_Hopcount,String node_State) {
    String[] strArr = neighbour_Ip.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
    try {
      logger.info("Node: "+node_Id+"- sendng hello to IP: {}"+ neighbour_Ip);
      logger.info("Node: "+node_Id+"- Sending Hello to node: {}"+id);
      logger.info("Node: "+node_Id+" - hopToSenderClusterhead: {}"+ node_Hopcount);
      logger.info("Node: "+node_Id+"- senderState:"+node_State);
      logger.info("Node: "+node_Id+"- senderClusterheadId: "+node_Cluster_head_Id);

      SendHello request = SendHello.newBuilder().setSenderId(node_Id).setHopToSenderClusterhead(node_Hopcount).setSenderState(node_State).setSenderClusterheadId(node_Cluster_head_Id).build();
      HelloResponse response = blockingStub.hello(request);
      logger.info("Node: "+node_Id+" - Got Response: {} after sending Hello to id: {}"+ id);
    }
    catch (RuntimeException e) {
      logger.error("Error with Node  " + node_Id);
      logger.error(e);
    } finally {
      channel.shutdown();
      Runtime.getRuntime().gc();
    }
  }
  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    CommunicatorClient client = new CommunicatorClient();
    try {

    } finally {
      client.shutdown();
    }
  }
}
