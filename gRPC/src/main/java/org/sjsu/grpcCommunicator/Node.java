package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import org.apache.log4j.Logger;
import java.util.*;


public class Node {

    final static Logger logger = Logger.getLogger(Node.class);

    String id;
    String parent_Id;
    String ip_address;
    List<String> child_list_Id;
    int dist;
    String cluster_head_Id;
    int hop_count;
    String rack_location;
    List<Integer> sub_tree_list;
    List<Integer> neighbour_list;
    int weight;
    int size;
    int child_request_counter;
    int initial_node_child_length ;
    String best_node_id;
    int best_node_hop_count;
    String best_node_cluster_head_Id;
    List<String>  neighbor_ID;



    String shift_Node_Id ;
    int shift_Node_Sum ;
    int shift_Node_Cluster ;

    int is_Cluster_head ;
    String state;

    NodeIdsList nodeIdsList;

    MongoClient mongo = new MongoClient("localhost", 27017);
    DB db = mongo.getDB("cmpe295Project");

    // get a single collection
    DBCollection collection = db.getCollection("spanningtree");

    public Node(int node_id){
        this.id = String.valueOf(node_id);
         this.nodeIdsList = new NodeIdsList();

        try {


            BasicDBObject query = new BasicDBObject();
            query.put("node_id", this.id);

            DBObject document = collection.findOne(query);
            ip_address =nodeIdsList.nodeIdsList.get(node_id);
            parent_Id = (String) document.get("parent_Id");
            child_list_Id = new ArrayList<String>();
            BasicDBList list = (BasicDBList)document.get("child_list_Id");
            for(Object el: list) {
                child_list_Id.add((String) el);
            }
            dist =(int)document.get("dist");
            cluster_head_Id=(String)document.get("cluster_head_Id");
            hop_count=0;
            rack_location= (String)document.get("rack_location");

            sub_tree_list = new ArrayList<Integer>();
            list = (BasicDBList)document.get("sub_tree_list");
            for(Object el: list) {
                sub_tree_list.add((int) el);
            }

            neighbour_list = new ArrayList<Integer>();
            list = (BasicDBList)document.get("sub_tree_list");
            for(Object el: list) {
                neighbour_list.add((int) el);
            }

            //weight = weightMatrix.getWeight(id)
            size= weight;
            child_request_counter=0;
            initial_node_child_length=0;
            best_node_id=id;
            best_node_hop_count = hop_count;
            best_node_cluster_head_Id=cluster_head_Id;

            neighbor_ID = new ArrayList<String>();
            get_Neighbors();

            initial_node_child_length= child_list_Id.size();
            shift_Node_Sum=0;
            shift_Node_Cluster=0;
            shift_Node_Id=null;

            is_Cluster_head=(int) document.get("is_Cluster_head");
            state=(String) document.get("state");




        }catch (Exception e){
            System.out.println(e);
        }


    }


    public void get_Neighbors(){
        String rack_row= rack_location.split(",")[0];
        String rack_column= rack_location.split(",")[0];
        List<String> my_Neighbors_Rack = new ArrayList<String>();
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row)+1)+","+String.valueOf(Integer.parseInt(rack_column))+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row)-1)+","+String.valueOf(Integer.parseInt(rack_column))+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row))+","+String.valueOf(Integer.parseInt(rack_column)+1)+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row))+","+String.valueOf(Integer.parseInt(rack_column)-1)+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row)+1)+","+String.valueOf(Integer.parseInt(rack_column)+1)+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row)-1)+","+String.valueOf(Integer.parseInt(rack_column)-1)+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row)+1)+","+String.valueOf(Integer.parseInt(rack_column)-1)+"");
        my_Neighbors_Rack.add(""+String.valueOf(Integer.parseInt(rack_row)-1)+","+String.valueOf(Integer.parseInt(rack_column)+1)+"");

        for(String el: my_Neighbors_Rack){
            try{
                BasicDBObject query = new BasicDBObject();
                query.put("rack_location",el );

                DBObject document = collection.findOne(query);;
                if(document!=null)
                  this.neighbor_ID.add((String) document.get("parent_Id"));
                else
                    logger.info("Node: "+id+"- No node with rackLocation:"+el+" found!");

            }catch(Exception e){
                System.out.println(e);
            }
        }

    }



  public void send_size_to_parent(){
        if(this.parent_Id!=null){
            //client.phaseOneClusterStart(this,nodeIdsList.nodeIdsList.get(this.parent_Id));
        }else{
            logger.info("Node: %s - Setting myself as clusterhead as no parent found! "+id);
            this.is_Cluster_head=1;
            this.cluster_head_Id= id;
            this.state = "free";
            try{
                logger.info("Node: %s - Updating DB with size,hopcount variables "+id);
                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("is_Cluster_head", this.is_Cluster_head);
                newDocument.put("cluster_head_Id", this.cluster_head_Id);
                newDocument.put("parent_Id", null);
                newDocument.put("size", this.size);
                newDocument.put("hop_count", this.hop_count);
                newDocument.put("state", this.state);

                BasicDBObject searchQuery = new BasicDBObject().append("nodeId", this.id);

                collection.update(searchQuery, newDocument);
                logger.info("Node: %s - Successfully DB with size,hopcount variables");
            }catch(Exception e){
                logger.error("Some Error occurred in sendSizeToParent()");
               System.out.println(e);
            }
            //client.send_Cluster(this);
        }
  }



}

