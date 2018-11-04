package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.*;

public class InitScript {
    final static Logger logger = Logger.getLogger(InitScript.class.getName());

    public static void main(String[] args){
        cleanDB();
        buildDB();
        spawnNode(0);
    }

    public static void cleanDB() {
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("cmpe295Project");
            DBCollection collection = db.getCollection("spanningtree");
            BasicDBObject document = new BasicDBObject();

            // Delete All documents from collection Using blank BasicDBObject
            collection.remove(document);
            logger.info("Cleaning DB if it exists");
        }
        catch(Exception e){
            logger.error(e);
//            logger.error(traceback.format_exc());
        }

    }

    public static void buildDB() {
        logger.info("Creating New DB");
        //make file to populate a tree
//        spanningtreepopulate.populateTree()
    }

    public static void spawnNode(int id) {
        Node node = new Node(id);
        CommunicatorServer cms = new CommunicatorServer();
        cms.serve(node);
        return;
    }
}

