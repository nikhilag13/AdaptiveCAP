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

  /**
   * Construct client connecting to HelloWorld server at {@code host:port}.
   */
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

  /**
   * Start stage one clustering.
   **/
  public void startStageOneCluster(Node node, String ipAddress) {
    String[] strArr = ipAddress.split(":");
    String host = strArr[0];
    int port = Integer.valueOf(strArr[1]);
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext(true)
            .build();
    blockingStub = CommunicatorGrpc.newBlockingStub(channel);
    //CommunicatorClient(nid, port);//port num?
    try {
      sendSize(node, blockingStub);

    } catch (RuntimeException e) {
      logger.error("Node:{} - {}".format(node.getId(), e));
      logger.error(e);

    } finally {

      channel.shutdown();
//      blockingStub = "None";
//      channel = "None";
      Runtime.getRuntime().gc();
    }
  }


  public void sendSize(Node node, CommunicatorGrpc.CommunicatorBlockingStub blockingStub) {

    System.out.println(node.getSize());
    logger.info("Node: %s - Starting function sendSize" + (node.getId()));
    MySize request = MySize.newBuilder().setSize(node.getSize()).build();
    AccomodateChild response = blockingStub.size(request);
    String sizeRPC = response.getMessage();
    logger.info("Node:" + node.getId() + " - Successfully sent the size message of size" + node.getSize() + " to parentId:" + node.getParent_Id());
    logger.info("Node:" + node.getParent_Id() + "- Responded to Size RPC with reply:" + sizeRPC);

    DBCollection collection = database.getCollection("spanningtree");

    BasicDBObject query = new BasicDBObject();
    query.put("nodeId", node.getId());

    if (sizeRPC == "Prune") {
      logger.info("Node:" + node.getId() + " - Got Prune");
      // Become a clusterhead and send Cluster RPC to children
      node.setCluster_head_Id(node.getId());
      node.setParent_Id("None");
      // Set I am the cluster
      node.setIs_Cluster_head(1);
      node.setState("free");
      try {

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("'is_Cluster_head'", node.getIs_Cluster_head());
        newDocument.put("'parent_Id'", node.getParent_Id());
        newDocument.put("'cluster_head_Id'", node.getCluster_head_Id());
        newDocument.put("'hop_count'", node.getHop_count());
        newDocument.put("'size'", node.getSize());
        newDocument.put("'state'", node.getState());

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        collection.update(query, updateObject);

      } catch (RuntimeException e) {
        logger.error("Node:" + node.getId() + "- not able to update db");
        logger.error(e);
      }
      //sendCluster(node);
    } else {
      logger.info("Node:" + node.getId() + "Didn't get Prune");
      //Do nothing if the child is accepted into the current cluster
      //Might need to add cluster ID to the central lookup #Later
    }
  }

  public void sendCluster(Node node) {

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
        JoinClusterRequest request = JoinClusterRequest.newBuilder().setClusterHeadName(node.getId()).build();
        request.newBuilder().setHopcount(hopCount).build();
        JoinClusterResponse response = blockingStub.joinCluster(request);

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
