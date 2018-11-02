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

    public Node(int node_id){
        this.id = String.valueOf(node_id);
        NodeIdsList nodeIdsList = new NodeIdsList();

        try {

            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("cmpe295Project");

            // get a single collection
            DBCollection collection = db.getCollection("spanningtree");
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
            //get_Neighbors();

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




}

