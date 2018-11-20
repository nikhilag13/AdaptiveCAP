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
    WeightMatrix weigthMatrix = new WeightMatrix();


//    Commented Check later
//
//    today_date = str(datetime.datetime.now()).split(" ")[0]
//    current_path = os.path.dirname(os.path.realpath(_file_))


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
//        logger.info("printing channel " + channel);
        blockingStub = CommunicatorGrpc.newBlockingStub(channel);

        StartPhase2ClusteringRequest request = StartPhase2ClusteringRequest.newBuilder().setStartPhase2("Start Phase 2 ").build();

        StartedPhase2ClusteringResponse response = blockingStub.startPhase2Clustering(request);
        String clusterRPC = response.getStartedPhase2ClusteringResponse();

        logger.info("RaspberryPi got following response after sending Hello to node id: %s " + key);
        logger.info(clusterRPC);

    }

    public void checkPhaseEnergy(String key, String value, int flag) {

        BasicDBObject query = new BasicDBObject();
        query.put("node_id", key);
        DBObject document = collection.findOne(query);
        int is_Cluster_head = 0;

        if (document == null) {
            return;
        }

        if((document.get("is_Cluster_head")!=null)) {
            is_Cluster_head = (int) document.get("is_Cluster_head");
        }

        if(is_Cluster_head == 1) {
            try {
                query = new BasicDBObject();
                query.put("cluster_head_Id", key);
                List<DBObject> myList = null;
                DBCursor myCursor = collection.find(query);
                myList = myCursor.toArray();
                int energy = 0;

                logger.info("1_cur2:{} " +myCursor);

                List<String> all_nodes = new ArrayList<String>();
                for(DBObject obj : myList){
                    all_nodes.add((String) obj.get("node_id"));
                }
                //logger.info("Node: {} All Nodes in this cluster:{}" +key+ " " +all_Nodes);

                for(DBObject obj : myList) {
                    int hops =  (int) obj.get("hop_count");
                    String this_nodeid = (String) obj.get("node_id");
                    int weight = 0;
                    for(String n : all_nodes){
                        weight = weight+ weigthMatrix.matrix[Integer.parseInt(this_nodeid)][Integer.parseInt(n)];
                    }
                    logger.info("Node: {} weight of node:{} is {} " + key + " "+ this_nodeid+" "+ weight);
                    energy += weight*hops;
                    logger.info("Node: {} energy: {} "+ key +" "+energy);
                }

                if(flag == 1) {
                    query = new BasicDBObject();
                    query.put("node_id", key);

                    BasicDBObject newDocument = new BasicDBObject();
                    newDocument.put("init_energy", energy);
                    BasicDBObject updateObject = new BasicDBObject();
                    updateObject.put("$set", newDocument);

                    collection.update(query, updateObject);
                }
                else{
                    query = new BasicDBObject();
                    query.put("node_id", key);

                    BasicDBObject newDocument = new BasicDBObject();
                    newDocument.put("final_energy", energy);
                    BasicDBObject updateObject = new BasicDBObject();
                    updateObject.put("$set", newDocument);

                    collection.update(query, updateObject);
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