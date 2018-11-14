
package org.sjsu.grpcCommunicator;


import com.mongodb.*;
import org.apache.http.util.ExceptionUtils;
import org.apache.log4j.Logger;
import java.util.*;
import java.lang.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

public class StageTwoStart {

    private static final Logger logger = Logger.getLogger(StageTwoStart.class.getName());

    private ManagedChannel channel;
    private CommunicatorGrpc.CommunicatorBlockingStub blockingStub;

    MongoClient mongoClient = new MongoClient("localhost", 27017);
    DB database = mongoClient.getDB("cmpe295Project");
    DBCollection collection = database.getCollection("spanningtree");

    NodeIdsList nodeIdsList = new NodeIdsList();


//    Commented Check later
//
//    today_date = str(datetime.datetime.now()).split(" ")[0]
//    current_path = os.path.dirname(os.path.realpath(__file__))


//        debug_handler = logging.handlers.RotatingFileHandler(os.path.join(current_path+"/rpilogs/", today_date+'-debug.log'),maxBytes=30000000,backupCount=40)
//        debug_handler.setLevel(logging.DEBUG)
//
//        info_handler = logging.handlers.RotatingFileHandler(os.path.join(current_path+"/rpilogs/", today_date+'-info.log'),maxBytes=30000000,backupCount=40)
//        info_handler.setLevel(logging.INFO)
//
//        error_handler = logging.handlers.RotatingFileHandler(os.path.join(current_path+"/rpilogs/", today_date+'-error.log'),maxBytes=300000,backupCount=40)
//        error_handler.setLevel(logging.ERROR)
//
//        formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
//        info_handler.setFormatter(formatter)
//        error_handler.setFormatter(formatter)
//        debug_handler.setFormatter(formatter)
//
//        logger.addHandler(info_handler)
//        logger.addHandler(error_handler)
//        logger.addHandler(debug_handler)

    public void hit_GRPC(String key, String value) {

        String[] strArr = value.split(":");
        String host = strArr[0];
        int port = Integer.valueOf(strArr[1]);
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();
        logger.info("printing channel " + channel);
        blockingStub = CommunicatorGrpc.newBlockingStub(channel);

        StartPhase2ClusteringRequest request = StartPhase2ClusteringRequest.newBuilder().setStartPhase2("Start Phase 2 ").build();
        StartedPhase2ClusteringResponse response = blockingStub.startPhase2Clustering(request);
        String clusterRPC = response.getStartedPhase2ClusteringResponse();

        logger.info("RaspberryPi got following response after sending Hello to node id: %s " + key);
        logger.info(clusterRPC);

    }

    public void checkPhaseEnergy(String key, String value, int flag) {
        //Node node = new Node();
        BasicDBObject query = new BasicDBObject();
        query.put("node_id", key);
        DBObject document = collection.findOne(query);
        int is_Cluster_head;

        if (document == null) {
            return;
        }
        if((document.get("is_Cluster_head")!=null)) {
             is_Cluster_head = (int) document.get("is_Cluster_head");
        }
        if(is_Cluster_head == 1) {
            try {

                BasicDBObject curQuery = new BasicDBObject();
                curQuery.put("clusterheadId", key);
                DBCursor cursor2 = collection.find(curQuery);
                int energy = 0;
                logger.info("1_cur2:{} " +cursor2);




//                cur = [c for c in cur2]
//                logger.info("2_cur2:{}".format(cur2))
//
//
//                allNodes = [n['nodeId'] for n in cur]
//
//
//                logger.info("Node: {} All Nodes in this cluster:{}".format(key, allNodes))

                WeightMatrix wm = new WeightMatrix();
                while (cursor2.hasNext()) {
                    DBObject document2 = cursor2.next();
                    int hops = (int)document2.get("hop_count");
                    String nodeId = (String)document2.get("node_id");
                    int weight = 0;
                    for(String n : all_Nodes ) {
                     weight = weight + wm.matrix[Integer.valueOf(nodeId)][Integer.valueOf(n)];
                     logger.info("Node: {} weight of node:{} is {} " +key+"," +nodeId+ ","+weight);
                     energy += weight * hops;
                     logger.info("Node: {} energy: {}" +key+ "," +energy);
                    }
                }
//                for document in cur:
//                hops = document['hopcount']
//                thisNodeId = document['nodeId']
//                weight = 0
//                for n in allNodes:
//                weight += weightMatrix.matrix[int(thisNodeId)][int(n)]
//                logger.info("Node: {} weight of node:{} is {}".format(key, thisNodeId, weight))
//                energy += weight * hops
//                logger.info("Node: {} energy: {}".format(key, energy))

                if(flag == 1){

                    //DBCollection collection = database.getCollection("spanningtree");

                    BasicDBObject query1 = new BasicDBObject();
                    query1.put("node_id", key);
                    BasicDBObject newDocument = new BasicDBObject();

                    newDocument.put("initenergy", energy);

                    BasicDBObject updateObject = new BasicDBObject();
                    updateObject.put("$set", newDocument);

                    collection.update(query1, updateObject);
                }
                else{

                    BasicDBObject query1 = new BasicDBObject();
                    query1.put("node_id", key);
                    BasicDBObject newDocument = new BasicDBObject();

                    newDocument.put("finalenergy", energy);

                    BasicDBObject updateObject = new BasicDBObject();
                    updateObject.put("$set", newDocument);

                    collection.update(query1, updateObject);

                }

            } catch (RuntimeException e) {
                logger.error("Error in calculateClusterEnergy");
                logger.error(e);
            }
        }

    }

    public void run() {

        logger.info("All ID_IP Mapping are as per below");
        logger.info(nodeIdsList.getNodeIdsList());
        boolean counter = false;

        for (HashMap.Entry<String, String> entry : nodeIdsList.getNodeIdsList().entrySet()) {
            logger.info("RaspberryPi is now checking Initial Energy Values for %s, at IP: %s" + entry.getKey() + "," + entry.getValue());
            checkPhaseEnergy(entry.getKey(), entry.getValue(), 1);
        }

        for (HashMap.Entry<String, String> entry : nodeIdsList.getNodeIdsList().entrySet()) {
            logger.info("RaspberryPi sending StartPhase 2 clustering to %s, at IP: %s" + entry.getKey() + "," + entry.getValue());
            System.out.println("RaspberryPiaspberryPi sending StartPhase 2 clustering to %s, at IP: %s" + entry.getKey() + "," + entry.getValue());
            hit_GRPC(entry.getKey(), entry.getValue());
        }

        for (HashMap.Entry<String, String> entry : nodeIdsList.getNodeIdsList().entrySet()) {
            logger.info("RaspberryPi is now checking Final Energy Values for %s, at IP: %s" + entry.getKey() + "," + entry.getValue());
            checkPhaseEnergy(entry.getKey(), entry.getValue(), 2);
        }
    }


    public static void main(String[] args) throws Exception {
        StageTwoStart stage2 = new StageTwoStart();
        try {
            stage2.run();
        } finally {

        }
    }
}