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

//import java.util.logging.Logger;

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



  MongoClient mongoClient = new MongoClient("localhost", 27017);
  DB database = mongoClient.getDB("cmpe295Project");

//  CommunicatorServer(Node node){
//    this.node = node;
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


    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
      HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

      public void joinCluster(JoinClusterRequest request,StreamObserver<JoinClusterResponse> responseObserver){

          //logger.debug("Node:%s - Server got Cluster message"%(self.node.id))
          String clusterName = request.getClusterHeadName();
          int hopCount = request.getHopcount();



          DBCollection collection = database.getCollection("spanningtree");

          BasicDBObject query = new BasicDBObject();
          query.put("nodeId", node.getId());

          try {

              BasicDBObject newDocument = new BasicDBObject();
              newDocument.put("'hop_count'",hopCount);
              newDocument.put("'cluster_head_Id'", clusterName);
              newDocument.put("'size'", node.getSize());
              newDocument.put("'is_Cluster_head'", 0);
              newDocument.put("'state'", "active");

              BasicDBObject updateObject = new BasicDBObject();
              updateObject.put("$set", newDocument);

              collection.update(query, updateObject);


          }catch (Exception e){
              logger.error(e);

              //    print("Node: %s is now joining Clusterleader Node: %s"%(str(self.node.id),str(clusterName)))
              //   logger.info("Node: %s - Now joining Clusterleader Node: %s"%(str(self.node.id),str(clusterName)))
              //   logger.info("Node: %s - current hop count: %s"%(str(self.node.id),self.node.hopcount))
          }

          if(node.getChild_list_Id() != null) {
              //logger.info("Node:%s - Children Found! Starting ClusterheadId Propagation" % (self.node.id))
              //thread3 = threading.Thread(target = self.node.propogateClusterheadInfo, args = (clusterName, hopCount))
              //thread3.start()
              //#time.sleep(2)
          }else{
              // logger.info("Node: %s - NO children found!"%(self.node.id))
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

          DBCollection collection = database.getCollection("spanningtree");

          BasicDBObject query = new BasicDBObject();
          query.put("nodeId", node.id);

          try {
              if (node.getSize() + childSize > THRESHOLD_S) {
                  node.setChild_request_counter(node.getChild_request_counter()+1);
//
                  // Move removing the child above sendSizeToParent as parent might send cluster but child needs to be removed
                  // Case of Node 0 and Node 1 (12 node cluster)
                  try {

                      BasicDBObject newDocument = new BasicDBObject();
                      newDocument.put("'child_list_Id'", node.getChild_list_Id());

                      BasicDBObject updateObject = new BasicDBObject();
                      updateObject.put("$set", newDocument);

                      collection.update(query, updateObject);

                  } catch (Exception e) {
                      logger.error("***");
                      logger.error("ERROR OCCURRED WHILE KICKING CHILDREN");
//                logger.error("Node id: %s was kicking child %s from childList" + (node.id, req.nodeId));
                      logger.error(e); //prints exception in the logger
                      logger.error("***");
                  }

                  logger.info("Node: " + node.id + " - Removed child " + req.getNodeId() + " from childList");
                  logger.info("Node: " + (node.getId()) + " - Sending Prune after checking if all children responded or not");
                  if (node.getChild_request_counter() == node.getInitial_node_child_length()) {
                      logger.info("Node: %s - All children responded. Sending size to parent" + (node.getId()));

                      /** below line is running method sendSizetoparent as background thread,
                       while the rest of the application continues it’s work.**/

//                    Thread thread1 = new Thread(new Runnable() {
//
//                        public void run() {
//                            node.sendSizeToParent(); //will be implemented in Node class
//                        }
//                    }).start();
                    }
                  logger.info("Node: " + node.id + "  - Sending Prune to childId: " + req.getNodeId());
                  AccomodateChild reply = AccomodateChild.newBuilder().setMessage("Prune ").build();
                  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
              } else {
                  logger.info("Node: "+(node.id)+" - Sending Accept to childId: "+req.getNodeId()+" after checking if all children responded or not");
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
//                        public void run() { node.sendSizeToParent(); //will be implemented in Node class
//                        }
//                    }).start();

                      logger.info("Node: " + node.getId() + " Sending accept to childId: " + req.getNodeId());
                      AccomodateChild reply = AccomodateChild.newBuilder().setMessage("Accepted ").build();
                      responseObserver.onNext(reply);
                      responseObserver.onCompleted();

                  }
              }
          } catch (Exception e) {
              logger.error("***");
          }
      }
  }
}
