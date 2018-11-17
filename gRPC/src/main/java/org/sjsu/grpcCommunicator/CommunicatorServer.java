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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.*;

import static java.lang.Thread.sleep;


/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class CommunicatorServer {
  private static final Logger logger = Logger.getLogger(CommunicatorServer.class.getName());

  /* The port on which the server should run */
  private int port = 50051;
  private Server server;
  private Node node;

  private NodeIdsList idList = new NodeIdsList();

  MongoClient mongoClient = new MongoClient("localhost", 27017);
  DB database = mongoClient.getDB("cmpe295Project");
  DBCollection collection = database.getCollection("spanningtree");

//  CommunicatorServer(Node node){
//    node = node;
//  }

  private void start() throws Exception {
    server = ServerBuilder.forPort(this.port)
        .addService(CommunicatorGrpc.bindService(new CommunicatorServiceImpl()))
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        CommunicatorServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void serve(Node node){
    logger.info("Node: %s - Creating GRPC Server " +(node.getId()));

    try {
      logger.info("Node %s - Created GRPC Server"+(node.getId()));
      NodeIdsList nodeIdsList = new NodeIdsList();
      final CommunicatorServer server = new CommunicatorServer();
      server.node= node;
      server.port = Integer.parseInt(nodeIdsList.getNodeIdsList().get(node.getId()).split(":")[1]);

      server.start();
     // server.add_insecure_port(raspberryPi_id_list.ID_IP_MAPPING[node.id])
      logger.info("Node %s - Starting GRPC Server" + (node.getId()));
      node.start_phase_one_clustering();
      server.blockUntilShutdown();
    }
    catch(Exception e){
      logger.error(e);
    }
    try {
      while (true) {
        logger.info("Node: %s - GRPC Server started successfully. Entering forever listening mode..." + (node.id));
        System.out.println("Inside Forever while...");
        sleep(30);
      }
//        except KeyboardInterrupt:
//    server.stop(0)
      } catch (InterruptedException e1) {
      e1.printStackTrace();
      logger.error(e1);
    }
  }


  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws Exception {
    final CommunicatorServer server = new CommunicatorServer();
    server.start();
    server.blockUntilShutdown();

  }

  private  class CommunicatorServiceImpl implements CommunicatorGrpc.Communicator {

    public void setStateDB(){
        //update state of the node in db
//        DBCollection collection = database.getCollection("spanningtree");
        BasicDBObject query = new BasicDBObject();
        query.put("node_id", node.id);

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("state", node.getState());

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        collection.update(query, updateObject);

    }


//      @Override
//      public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
//          HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
//          responseObserver.onNext(reply);
//          responseObserver.onCompleted();
//      }

      @Override
      public void joinCluster(JoinClusterRequest request, StreamObserver<JoinClusterResponse> responseObserver) {

          logger.info("Node:%s - Server got Cluster message " + String.valueOf(request.getHopcount()));
          String clusterName = request.getClusterHeadName();
          int hopCount = request.getHopcount();


//          DBCollection collection = database.getCollection("spanningtree");

          BasicDBObject query = new BasicDBObject();
          query.put("node_id", node.getId());

          try {

              BasicDBObject newDocument = new BasicDBObject();
              newDocument.put("hop_count", hopCount);
              newDocument.put("cluster_head_Id", clusterName);
              newDocument.put("size", node.getSize());
              newDocument.put("is_Cluster_head", 0);
              newDocument.put("state", "active");

              BasicDBObject updateObject = new BasicDBObject();
              updateObject.put("$set", newDocument);

              collection.update(query, updateObject);


          } catch (Exception e) {
              logger.error(e);
          }

          logger.info("Node: "+ node.getId()+" - Now joining Clusterleader Node: %s"+ clusterName);
          logger.info("Node: "+ node.getId()+" - current hop count: %s" + node.getHop_count());

          if (node.getChild_list_Id() != null) {
              logger.info("Node: "+ node.getId()+" - Children Found! Starting ClusterheadId Propagation" );
              //thread3 = threading.Thread(target = self.node.propogateClusterheadInfo, args = (clusterName, hopCount))
              //thread3.start()
              //#time.sleep(2)
              node.propogate_Cluster_head_Info(clusterName, hopCount);
          } else {
               logger.info("Node: "+ node.getId()+" - NO children found!");
          }
          JoinClusterResponse reply = JoinClusterResponse.newBuilder().setJoinClusterResponse("joined").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();
//          return JoinClusterResponse(joinClusterResponse="Joined");
      }

      @Override
      public void size(MySize req, StreamObserver<AccomodateChild> responseObserver) {
          int childSize = req.getSize();
         int THRESHOLD_S = 150; //keep in it different file

          node.getChild_list_Id().remove(req.getNodeId()); //remove from arraylist childListId
//
          collection = database.getCollection("spanningtree");
          BasicDBObject query = new BasicDBObject();
          query.put("node_id", node.getId());

          logger.info("Node: " + node.getId() + " - Current size: %s "+ node.getSize());


          try {

              logger.info("Node: " + node.getId() + " - Child Node: "+ req.getNodeId() + "has size %s" + childSize);
              if (node.getSize() + childSize > idList.getTHRESHOLD_S()) {
//              if ((node.getSize() + childSize) > THRESHOLD_S) {
                  logger.info("inside if");

                  node.setChild_request_counter(node.getChild_request_counter() + 1);
                  // Move removing the child above sendSizeToParent as parent might send cluster but child needs to be removed
                  // Case of Node 0 and Node 1 (12 node cluster)
                  try {

                      logger.info("updating node" );


                      BasicDBObject newDocument = new BasicDBObject();
                      newDocument.put("child_list_Id", node.getChild_list_Id());

                      BasicDBObject updateObject = new BasicDBObject();
                      updateObject.put("$set", newDocument);

                      collection.update(query, updateObject);

                  } catch (Exception e) {
                      logger.error("***");
                      logger.error("ERROR OCCURRED WHILE KICKING CHILDREN");
                      logger.error("Node id: " + node.getId() + "  was kicking child " + req.getNodeId() + " from childList");
                      logger.error(e); //prints exception in the logger
                      logger.error("***");
                  }

                  logger.info("Node: " + node.id + " - Removed child " + req.getNodeId() + " from childList " + childSize + " " + node.getSize());
                  logger.info("Node: " + (node.getId()) + " - Sending Prune after checking if all children responded or not");
                  if (node.getChild_request_counter() == node.getInitial_node_child_length()) {
                      logger.info("Node: %s - All children responded. Sending size to parent" + (node.getId()));

                      /** below line is running method sendSizetoparent as background thread,
                       while the rest of the application continues it’s work.**/

//                    Thread thread1 = new Thread(new Runnable() {
//
//                        public void run() {
//                            node.sendSizeToParent();
//                        }
//                    }).start();
                      node.send_size_to_parent();
                  }
                  logger.info("Node: " + node.id + "  - Sending Prune to childId: " + req.getNodeId());
                  AccomodateChild reply = AccomodateChild.newBuilder().setMessage("Prune").build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
              } else {
                  logger.info("Node: " + (node.id) + " - Sending Accept to childId: " + req.getNodeId() + " after checking if all children responded or not " + childSize + " " + node.getSize());
                  node.setSize(node.getSize() + childSize);
                  try {

                      BasicDBObject newDocument = new BasicDBObject();
                      newDocument.put("size", node.getSize());

                      BasicDBObject updateObject = new BasicDBObject();
                      updateObject.put("$set", newDocument);

                      collection.update(query, updateObject);

                  } catch (Exception e) {
                      logger.error("***");
                      logger.error("ERROR OCCURRED WHILE Accepting size Node " + (node.id));
                      logger.error(e);
//                logger.error(traceback.format_exc())
                      logger.error("***");
                  }

                  logger.info("Node " + node.id + ": - New size: " + node.getSize());
                  node.setChild_request_counter(node.getChild_request_counter() + 1);

                  if ((node.getChild_list_Id() != null) &&
                          (node.getChild_request_counter() == node.getInitial_node_child_length())) {
                      logger.info("Node: " + node.getId() + "  - All children responded. Sending size to parent");

//                    Thread thread2 = new Thread(new Runnable() {
//
//                        public void run() { node.sendSizeToParent();
//                        }
//                    }).start();

                      node.send_size_to_parent();

                      logger.info("Node: " + node.getId() + " Sending accept to childId: " + req.getNodeId());
                      AccomodateChild reply = AccomodateChild.newBuilder().setMessage("Accepted").build();
                      responseObserver.onNext(reply);
                      responseObserver.onCompleted();

                  }
              }
          } catch (Exception e) {
              logger.error(e);
          }
      }

      /* *************** Phase 2 - call from client SendHello() *******  **/
      @Override
      public void hello(SendHello req, StreamObserver<SendHelloResponse> responseObserver) {
          logger.info("Node: " + node.getId() + " catering to Hello message with request: " + req);
          if (node.getIs_Cluster_head() == 1) {
              // do nothing
              logger.info("Node: " + node.getId() + " - Got hello message from senderId: " + req.getSenderId());
              logger.info("Node: " + node.getId() + " - I am clusterhead. Sending Not interested");
              SendHelloResponse reply = SendHelloResponse.newBuilder().setInterested(-1).build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }
          if ((node.getState()).equals("active")) {
              logger.info("Node: " + node.getId() + " - State: active");
              (node.neighbour_Hello_Array).add(req.getSenderId());
              logger.info("Node: " + node.getId() + " - Printing neighbourHelloArray - " + node.neighbour_Hello_Array);

              if (req.getSenderId() == req.getSenderClusterheadId()) {
                  logger.info("Node: " + node.getId() + " - Got hello from Clusterhead. Adding it to neighbourArray. Returning -1");
                  //return -1
                  SendHelloResponse reply = SendHelloResponse.newBuilder().setInterested(-1).build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
              }
              logger.info("Node: " + node.getId() + " - hopCount= " + node.getHop_count() + " with bestHopCount: " + node.getBest_node_hop_count() +
                      " and senderId: " + req.getSenderId() + " has hopCount " + req.getHopToSenderClusterhead());

              if ((node.getCluster_head_Id() != req.getSenderClusterheadId()) && (node.getBest_node_hop_count() > (req.getHopToSenderClusterhead() + 1))) {
                  logger.info("Node: " + node.getId() + " - Updating bestNode as senderId: " + req.getSenderId() + " looks relevant choice as new parent");
                  //set the best node values
                  node.setBest_node_id(req.getSenderId());
                  node.setBest_node_hop_count(req.getHopToSenderClusterhead() + 1);
                  node.setBest_node_cluster_head_Id(req.getSenderClusterheadId());

                  if ((node.getNeighbour_Hello_Array().size() == node.getNeighbor_ID().size()) && (node.getBest_node_id() != node.getId())) {
                      //logger.info("Node: %s - Received hello messages from ALL neighbours. Sending shift node request to would be ex-clusterheadId:%s" + node_id, self.node.clusterheadId));
                      //logger.info("Node: %s - State variables: bestNodeId: {},bestNodeClusterHeadId:{},current clusterheadId: {},current parent: {}".format(self.node.bestNodeId, self.node.bestNodeClusterHeadId, self.node.clusterheadId, self.node.parentId));

                      //uncomment it later
                      node.send_shift_node_request(node.getBest_node_cluster_head_Id());
                  }
                  logger.info("Node: " + node.getId() + "- Sending interested response for senderId: " + req.getSenderId());

                  SendHelloResponse reply = SendHelloResponse.newBuilder().setInterested(1).build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
              }

              if ((node.getNeighbour_Hello_Array().size() == node.getNeighbor_ID().size()) && (node.getBest_node_id() != node.getId())) {

                  //May need to add best_Node_Hop_Count in the sendShiftRPC to update node's hop count if request is accepted
                  logger.info("Node: " + node.getId() + " - Received hello messages from ALL neighbours. Sending shift node request to would be ex-clusterheadId:%s" + node.getCluster_head_Id());
                  logger.info("Node: " + node.getId() + " - State variables: bestNodeId: " + node.getBest_node_id() + ",bestNodeClusterHeadId: "
                          + node.getBest_node_cluster_head_Id() + ",current clusterheadId: " + node.getCluster_head_Id() + ",current parent: " + node.getParent_Id());

                  //uncomment later
                  node.send_shift_node_request(node.getBest_node_cluster_head_Id());

              }
              //send interested -1
              logger.info("Node: " + node.getId() + " - Sending NOT interested response for senderId: " + req.getSenderId());
              SendHelloResponse reply = SendHelloResponse.newBuilder().setInterested(-1).build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          } else {
              logger.info("Node: " + node.getId() + " - Sending NOT interested response for senderId: " + req.getSenderId());

              SendHelloResponse reply = SendHelloResponse.newBuilder().setInterested(-1).build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }
      }

      /* *******Phase 2 - call from client.sendShiftNodeRequest()************************* */
      @Override
      public void shiftNodeRequest(ShiftRequest req, StreamObserver<ShiftResponse> responseObserver) {

          logger.info("Node: " + node.getId() + " - Clusterhead got ShiftNodeRequest from node id: " + req.getNodeId());
          if (node.getIs_Cluster_head() == 1 && (node.getState()).equals("free")) {
              // set the variables of this node
              node.setState("busy");
              node.setShift_Node_Id(req.getNodeId());
              node.setShift_Node_Sum(req.getSumOfweight());
              node.setShift_Node_Cluster(req.getClusterHeadId());

              //send Jam request
              try {
                  //update the db with recently changed node state
                  setStateDB();

              } catch (Exception e) {
                  logger.error(e);
                  logger.error("Error occurred in ShiftNodeRequest while submitting data");
              }

              logger.info("Node: " + (node.id) + " - ClusterheadId sending Jam Signal across its cluster");

              //call Jam signal
              //uncomment later
              node.send_jam_signal();

              //send shift cluster request to Cj
              logger.info("Node: " + (node.id) + " - ClusterheadId successfully sent Jam Signal across its cluster");
              logger.info("Node: " + (node.id) + " - ClusterheadId now sending sendShiftClusterRequest");

              // uncomment later
              node.send_shift_cluster_request();

              ShiftResponse reply = ShiftResponse.newBuilder().setMessage("Recieved ShiftNode Request").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          } else {
              logger.info("Node: " + node.getId() + " - ClusterheadId not free for accomodating ShiftNodeRequest from node id: " + req.getNodeId());
              ShiftResponse reply = ShiftResponse.newBuilder().setMessage("Not approving ShiftNode Request").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }

      }

      /* *************** Phase 2 - call from client sendJamSignal() and propagateJamToChildren() *******  **/
      public void jam(JamRequest req, StreamObserver<JamResponse> responseObserver) {
          String jamId = req.getNodeId();
          logger.info("Node: " + node.getId() + " - Received Jam signal from clusterheadId: " + jamId);
          if (node.getIs_Cluster_head() != 1) {
              logger.info("Node: " + (node.id) + " - Going to sleep zzzzzzzz");
              node.setState("sleep");

              try {
                  //update state in the db
                  setStateDB();
              } catch (Exception e) {
                  logger.error(e);
              }

              logger.info("Node: " + node.getId() + " - Sending jam to all children");

              //uncomment later
              node.propogate_jam_to_children(jamId);

              logger.info("Node: " + node.getId() + " - Successfully propagated jam to all children");

          }
          JamResponse reply = JamResponse.newBuilder().setJamResponse("jammed").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();
      }



      /* ************* Phase 2 ********************* */
      @Override
      public void shiftClusterRequest(ShiftClusterReq req, StreamObserver<ShiftClusterRes> responseObserver) {
          if (node.getIs_Cluster_head() == 1 && (node.getState()).equals("free")) {
              // check size bound condition
              if (node.getSize() + req.getSumOfweights() > idList.getTHRESHOLD_S()) {
                  //send reject to Ci
                  logger.info("Node: " + node.getId() + " Rejecting ShiftClusterRequest from clusterheadId: "
                          + req.getSenderClusterHeadId() + "regarding node: " + req.getSenderNodeId());

                  node.reject(req.getSenderClusterHeadId());
                  ShiftClusterRes reply = ShiftClusterRes.newBuilder().setMessage("Rejecting").build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();

              } else {
                  node.setShift_Node_Id(req.getSenderNodeId());
                  node.setShift_Node_Cluster(req.getSenderClusterHeadId());
                  node.setShift_Node_Sum(req.getSumOfweights());

                  //set state to busy for the node
                  node.setState("busy");

                  //now, send Jam to all nodes in the cluster
                  try {
                      //update the db with recently changed node state
                       setStateDB();
                  } catch (Exception e) {
                      logger.error(e);
                  }
                  // accept to Ci

                  //uncomment later
                  node.send_jam_signal();

                  logger.info("Node: " + node.getId() + " - Accepting ShiftClusterRequest from clusterheadId: " + req.getSenderClusterHeadId() + " regarding node: " + req.getSenderNodeId());

                  //uncomment later
                  node.accept(req.getSenderClusterHeadId());

                  ShiftClusterRes reply = ShiftClusterRes.newBuilder().setMessage("Accepting").build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();

              }
          }
            else {
              //shifting is already done, send rejecting message
              //uncomment later
             node.reject(req.getSenderClusterHeadId());

              ShiftClusterRes reply = ShiftClusterRes.newBuilder().setMessage("Rejecting").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }
      }

      public void accept(AcceptRequest req, StreamObserver<AcceptResponse> responseObserver) {
          if((node.getState()).equals("busy")){
              logger.info("Node: " + node.getId() + "- Accept Request received from clusterhead: "+ req.getClusterHeadId());
              if(node.check_energy()){
                  node.send_shift_start();
                  AcceptResponse reply = AcceptResponse.newBuilder().setMessage("Starting Shift Start").build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
              }
              else{
                  node.send_wakeup();
                  node.send_shift_finished();
                  node.setState("free");
                  try{
                      setStateDB();
                  }
                  catch(Exception e){
                      logger.error(e);
                      logger.error("Error Occurred in Node: " +(node.getId()));
                  }

                  AcceptResponse reply = AcceptResponse.newBuilder().setMessage("Starting Shift Finished").build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
              }

          }
          else{
              AcceptResponse reply = AcceptResponse.newBuilder().setMessage("Not in busy state for now !").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }

      }

      public void reject(RejectRequest req, StreamObserver<RejectResponse> responseObserver) {

          if(node.getState().equals("busy")){

              node.setState("free");
              try{
                  setStateDB();
              }
              catch(Exception e){
                  logger.error(e);
                  logger.error("Error Occurred in Node: " +(node.getId()));
              }

              node.send_wakeup();

              RejectResponse reply = RejectResponse.newBuilder().setMessage("Thanks for Rejecting").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }
          else{
              RejectResponse reply = RejectResponse.newBuilder().setMessage("Not in busy state for now !").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }

      }

      public void wakeUp(WakeUpRequest req, StreamObserver<WakeUpResponse> responseObserver) {
          if(node.getState().equals("sleep")){
              node.setState("active");
              try{
                  setStateDB(); //a call to change state in the db
              }
              catch(Exception e){
                  logger.error(e);
              }

              //uncomment later
              node.propogate_wake_up();

              WakeUpResponse reply = WakeUpResponse.newBuilder().setWokenUp("wokeup").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }
          else{

              //uncomment later
              node.propogate_wake_up();
              WakeUpResponse reply = WakeUpResponse.newBuilder().setWokenUp("already").build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }
      }
      public void shiftStart(ShiftStartRequest req, StreamObserver<ShiftStartResponse> responseObserver) {
            if(node.getId() == req.getTargetNodeId() && node.getState().equals("sleep")){
                String oldClusterheadId = node.getCluster_head_Id();

                //uncomment later
                node.say_bye_to_parent();
                node.update_internal_variables_and_send_join(node.getBest_node_id(), node.getBest_node_cluster_head_Id(), node.getBest_node_hop_count() + 1);

                 // self.node.propagateNewClusterHeadToChildren()
                 // is sendShiftCompleteToBothClusterHeads it necessary - can remove if not needed

                node.send_shift_complete_to_both_cluster_heads(oldClusterheadId, node.getCluster_head_Id());

                ShiftStartResponse reply = ShiftStartResponse.newBuilder().setShiftStartResponse("byebye").build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
            else{
                ShiftStartResponse reply = ShiftStartResponse.newBuilder().setShiftStartResponse("ShiftStart Sent to Wrong Node").build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
      }

      public void shiftFinished(ShiftFinishedRequest req, StreamObserver<ShiftFinishedResponse> responseObserver) {
          if(node.getState().equals("busy")){
              node.send_wakeup();
              try{
                  setStateDB(); //a call to change state in the db
              }
              catch(Exception e){
                  logger.error(e);
                  logger.error("Error Occurred in Node: " +(node.getId()));
              }
          }
          ShiftFinishedResponse reply = ShiftFinishedResponse.newBuilder().setMessage("Finished").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();

      }

      public void joinNewParent(JoinNewParentRequest req, StreamObserver<JoinNewParentResponse> responseObserver) {
          if(node.getState().equals("sleep")){

              if(node.child_list_Id == null)
                  node.child_list_Id = new ArrayList<String>();

              node.child_list_Id.add(req.getNodeId());
              int sizeIncrement = req.getChildSize();
              node.setSize(node.getSize() + req.getChildSize()) ;
              node.inform_parent_about_new_size(sizeIncrement);

          }
          else if( node.getIs_Cluster_head() == 1  && node.getState().equals("busy")){
              node.child_list_Id.add(req.getNodeId());

              try{
                  //set child list id
//                  DBCollection collection = database.getCollection("spanningtree");
                  BasicDBObject query = new BasicDBObject();
                  query.put("node_id", node.id); setStateDB();

                  BasicDBObject newDocument = new BasicDBObject();
                  newDocument.put("child_list_Id", node.getChild_list_Id());

                  BasicDBObject updateObject = new BasicDBObject();
                  updateObject.put("$set", newDocument);

                  collection.update(query, updateObject);
              }
              catch(Exception e){
                  logger.error(e);
                  logger.error("Error Occurred in Node: " +(node.getId()));
              }

              int sizeIncrement = req.getChildSize();
              node.setSize(node.getSize() + req.getChildSize()) ;
          }

          JoinNewParentResponse reply = JoinNewParentResponse.newBuilder().setJoinResponse("welcome my new child").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();
      }

      public void updateSize(UpdateSizeRequest req, StreamObserver<UpdateSizeResponse> responseObserver) {
          node.setSize(node.getSize() + req.getSizeIncrement());

          try{
              //set node size
//              DBCollection collection = database.getCollection("spanningtree");
              BasicDBObject query = new BasicDBObject();
              query.put("node_id", node.id); setStateDB();

              BasicDBObject newDocument = new BasicDBObject();
              newDocument.put("size", node.getSize());

              BasicDBObject updateObject = new BasicDBObject();
              updateObject.put("$set", newDocument);

              collection.update(query, updateObject);
          }
          catch(Exception e){
              logger.error(e);
              logger.error("Error Occurred in Node: " +(node.getId()));
          }

          node.inform_parent_about_new_size(req.getSizeIncrement());

          UpdateSizeResponse reply = UpdateSizeResponse.newBuilder().setUpdateSizeResponse("updated size").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();
      }


      public void updateClusterhead(UpdateClusterheadRequest req, StreamObserver<UpdateClusterheadResponse> responseObserver) {

          node.setCluster_head_Id(req.getNewClusterheadId());

          try{
              //set node size
//              DBCollection collection = database.getCollection("spanningtree");
              BasicDBObject query = new BasicDBObject();
              query.put("node_id", node.id); setStateDB();

              BasicDBObject newDocument = new BasicDBObject();
              newDocument.put("cluster_head_Id", node.getCluster_head_Id());

              BasicDBObject updateObject = new BasicDBObject();
              updateObject.put("$set", newDocument);

              collection.update(query, updateObject);
          }
          catch(Exception e){
              logger.error(e);
              logger.error("Error Occurred in Node: " +(node.getId()));
          }


          node.propagate_new_cluster_head_to_children();

          UpdateClusterheadResponse reply = UpdateClusterheadResponse.newBuilder().setUpdateClusterheadResponse("clusterhead Updated").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();

      }

      public void shiftComplete(SendShiftCompleteAck req, StreamObserver<ClusterheadAckSendShift> responseObserver) {



          logger.info("Node: "+ node.getId() +" - Clusterhead got SendShiftComplete rpc with message:" +req.getSendShiftCompleteAck());
          logger.info("Node: "+ node.getId() +" - Clusterhead sending wakeup across its cluster");

          //uncomment later
          node.send_wakeup();
          logger.info("Node: "+ node.getId() +"- Clusterhead successfully sent wakeup across its cluster" );
          node.setState("free");
          try{
              setStateDB(); //a call to change state in the db
          }
          catch(Exception e){
              logger.error(e);
              logger.error("Error Occurred in Node: " +(node.getId()));
          }


          ClusterheadAckSendShift reply = ClusterheadAckSendShift.newBuilder().setClusterheadAckSendShift("ClusterheadId: %s acknowledged shift.."+ node.getId()).build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();
      }

      public void removeChildIdFromParent(RemoveChildIdFromParentRequest req, StreamObserver<RemoveChildIdFromParentResponse> responseObserver) {

          logger.info("Node: "+ node.getId() +" - As Parent got RemoveChildIdFromParent rpc from Node: %s" + req.getDepartingChildId());
          logger.info("Node: "+ node.getId() +" - As Parent has children BEFORE removal: " + node.child_list_Id);

          node.child_list_Id.remove(req.getDepartingChildId());


          BasicDBObject query = new BasicDBObject();
          query.put("node_id", node.id); setStateDB();

          try{
              //set child list i

              BasicDBObject newDocument = new BasicDBObject();
              newDocument.put("child_list_Id", node.getChild_list_Id());

              BasicDBObject updateObject = new BasicDBObject();
              updateObject.put("$set", newDocument);

              collection.update(query, updateObject);
          }
          catch(Exception e){
              logger.error(e);
              logger.error("Error Occurred in Node: " +(node.getId()));
          }

          logger.info("Node: "+ node.getId() +" - As Parent has children AFTER removal: {}" + node.child_list_Id);

          RemoveChildIdFromParentResponse reply = RemoveChildIdFromParentResponse.newBuilder().setRemoveChildIdFromParentResponse("Removed").build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();

      }

      public void startPhase2Clustering(StartPhase2ClusteringRequest req, StreamObserver<StartedPhase2ClusteringResponse> responseObserver) {
          logger.info("Node: "+ node.getId() +" - Got StartPhase2Clustering");
          //logger.info("Node: %s - Checking Initial Energy of cluster first"%(self.node.id))
          // self.node.calculateClusterEnergy()
          // logger.info("Node: %s - Now initiating phase2"%(self.node.id))
          node.start_phase2_clustering();
          String response = "Node: "+ node.getId()+" - Done with phase2 clustering";
          StartedPhase2ClusteringResponse reply = StartedPhase2ClusteringResponse.newBuilder().setStartedPhase2ClusteringResponse(response).build();
          responseObserver.onNext(reply);
          responseObserver.onCompleted();

      }

      public void checkEnergyDrain(CheckEnergyDrainRequest req, StreamObserver<CheckEnergyDrainResponse> responseObserver) {

          logger.info("Node: " + node.getId() + " - Got CheckEnergyDrain Request");
          if (node.getIs_Cluster_head() != 1) {
              logger.info("Node: " + node.getId() + " - Not a clusterhead Rejecting request");
              CheckEnergyDrainResponse reply = CheckEnergyDrainResponse.newBuilder().setCheckEnergyDrainResponse(-1).build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          } else {

              // uncomment later, check the type of drain
              //String drain = node.startCheckingEnergyDrain();
              CheckEnergyDrainResponse reply = CheckEnergyDrainResponse.newBuilder().setCheckEnergyDrainResponse(-1).build();
              responseObserver.onNext(reply);
              responseObserver.onCompleted();
          }

      }


  }

}
