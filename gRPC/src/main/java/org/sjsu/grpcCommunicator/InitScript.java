package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.*;
import java.lang.*;

public class InitScript {
    final static Logger logger = Logger.getLogger(InitScript.class.getName());

    class myRunnable implements Runnable {
        int id ;

        myRunnable(int id) {
            this.id =id;
        }


        public void run() {
            spawnNode(id);
        }
    }

    public void spawn() {
        for ( int i = 0; i < 8; i++) {

            Thread thread = new Thread(new myRunnable(i));
            thread.start();
        }
    }


    public static void main(String[] args) {

        InitScript initScript = new InitScript();
        initScript.cleanDB();
        initScript.buildDB();
       // initScript.spawn();
        initScript.spawnNode(0);



    }

    public  void cleanDB() {
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("cmpe295Project");
            DBCollection collection = db.getCollection("spanningtree");
            BasicDBObject document = new BasicDBObject();

            // Delete All documents from collection Using blank BasicDBObject
            collection.remove(document);
            logger.info("Cleaning DB if it exists");
        } catch (Exception e) {
            logger.error(e);
//            logger.error(traceback.format_exc());
        }

    }

    public  void buildDB() {
        logger.info("Creating New DB");
        //make file to populate a tree
//        spanningtreepopulate.populateTree()
    }

    public  void spawnNode( int id) {
        Node node = new Node(id);
        CommunicatorServer cms = new CommunicatorServer();
        cms.serve(node);
    }


}