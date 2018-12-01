package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;

public class InitScript {
    final static Logger logger = Logger.getLogger(InitScript.class.getName());
    int nodeCount = 12;

    class myRunnable implements Runnable {
        int id ;

        myRunnable(int id) {
            this.id =id;
        }


        public void run() {
//            logger.info("Run method for Node: "+String.valueOf(id));
            spawnNode(id);
        }
    }

    public void spawn() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();  //a list to store threads

        for ( int i = 0; i < nodeCount; i++) {
            Thread thread = new Thread(new myRunnable(i));
            threadList.add(thread);
//            thread.start();
        }
        for(Thread thread: threadList){ //run the thread with time delay
            thread.start();
            Thread.sleep(1000);
        }
    }


    public static void main(String[] args) throws InterruptedException {

        InitScript initScript = new InitScript();
        initScript.cleanDB();
         if(args.length>0)
            initScript.buildDB(args[0]);
         else
             initScript.buildDB("sparse");
         initScript.spawn();
    }

    public  void cleanDB() {
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("cmpe295Project");
            DBCollection collection = db.getCollection("spanningtree");
            collection.drop();
            logger.info("Cleaning DB if it exists");
        } catch (Exception e) {
            logger.error(e);
        }

    }

    public  void buildDB(String type) {
        logger.info("Creating New DB "+type);
        if(type.equals("dense")) {
            this.nodeCount= InsertSpanningTreeDense.nodeCount;
            InsertSpanningTreeDense.insertNodes();
        }
        else if (type.equals("medium")) {
            this.nodeCount= InsertSpanningTreeMedium.nodeCount;
            InsertSpanningTreeMedium.insertNodes();
        }
        else {
            this.nodeCount=InsertSpanningTree.nodeCount;
            InsertSpanningTree.insertNodes(); //populate tree
        }
    }

    public  void spawnNode( int id) {
//        logger.info("Spwaning new node "+String.valueOf(id));
        Node node = new Node(id);
    }


}