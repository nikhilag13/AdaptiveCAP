package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import com.mongodb.util.*;
import org.apache.log4j.Logger;

public class InsertSpanningTreeMedium {

    static int nodeCount =12;


    public static void insertNodes() {
        //insert spanning tree info
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("cmpe295Project");
        DBCollection collection = database.getCollection("spanningtree");

        String json = "{'node_id' : '0','parent_Id' : null, 'child_list_Id' : ['1', '2' ]," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : ['1', '2', '3' , '4' , '5', '6' ]," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '0,0' }";

        collection.insert((DBObject) JSON.parse(json));


        json = "{'node_id' : '1','parent_Id' : '0', 'child_list_Id' : ['3', '5' ]," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : ['3', '5' ]," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '0,1' }";


        collection.insert((DBObject) JSON.parse(json));


        json = "{'node_id' : '2','parent_Id' : '0', 'child_list_Id' : ['4', '6' ]," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : ['1', '2', '3' , '4' , '5', '6' ]," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '0,2' }";


        collection.insert((DBObject) JSON.parse(json));


        json = "{'node_id' : '3','parent_Id' : '1', 'child_list_Id' : null," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '1,0' }";

        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '4','parent_Id' : '2', 'child_list_Id' : ['9']," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '1,1' }";

        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '5','parent_Id' : '1', 'child_list_Id' : ['11']," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '1,2' }";

        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '6','parent_Id' : '2', 'child_list_Id' : ['7', '8']," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '2,1' }";


        collection.insert((DBObject) JSON.parse(json));


        json = "{'node_id' : '7','parent_Id' : '6', 'child_list_Id' : null," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '2,0' }";


        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '8','parent_Id' : '6', 'child_list_Id' : ['10']," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '2,2' }";


        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '9','parent_Id' : '4', 'child_list_Id' : null," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '0,3' }";


        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '10','parent_Id' : '8', 'child_list_Id' : null," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '1,3' }";

        collection.insert((DBObject) JSON.parse(json));

        json = "{'node_id' : '11','parent_Id' : '5', 'child_list_Id' : null," +
                "'dist' : 0 , 'cluster_head_Id': null, 'sub_tree_list' : null," +
                "'neighbour_list' : null ,weight : 0, 'child_weight_list' : null, 'is_Cluster_head' : 0," +
                "'state' : 'active', 'rack_location' : '2,3' }";

        collection.insert((DBObject) JSON.parse(json));
    }
}