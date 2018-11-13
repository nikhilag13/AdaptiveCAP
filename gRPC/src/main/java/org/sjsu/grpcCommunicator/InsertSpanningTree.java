package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import com.mongodb.util.*;
import org.apache.log4j.Logger;

public class InsertSpanningTree {



    public static void insertNodes(){
        //insert spanning tree info
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("cmpe295Project");
        DBCollection collection = database.getCollection("spanningtree");


        String json = "{\n" +
                "\t\"node_id\": \"0\",\n" +
                "\t\"parent_Id\": null,\n" +
                "\t\"child_list_Id\": [\"1\", \"2\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": [\"1\", \"2\", \"3\", \"4\", \"5\", \"6\"],\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"0,0\"\n" +
                "}";
        collection.insert((DBObject) JSON.parse(json));

        json = "{\n" +
                "\t\"node_id\": \"1\",\n" +
                "\t\"parent_Id\": \"0\",\n" +
                "\t\"child_list_Id\": [\"3\", \"5\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": [\"3\", \"5\"],\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"0,1\"\n" +
                "}";

        collection.insert((DBObject) JSON.parse(json));

        json = "{\n" +
                "\t\"node_id\": \"2\",\n" +
                "\t\"parent_Id\": \"0\",\n" +
                "\t\"child_list_Id\": [\"4\", \"6\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": [\"1\", \"2\", \"3\", \"4\", \"5\", \"6\"],\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"0,1\"\n" +
                "}";

        collection.insert((DBObject) JSON.parse(json));

        json = "{\n" +
                "\t\"node_id\": \"3\",\n" +
                "\t\"parent_Id\": \"1\",\n" +
                "\t\"child_list_Id\": null,\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": null,\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"1,0\"\n" +
                "}";
        collection.insert((DBObject) JSON.parse(json));


        json = "{\n" +
                "\t\"node_id\": \"4\",\n" +
                "\t\"parent_Id\": \"2\",\n" +
                "\t\"child_list_Id\": [\"9\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": null,\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"1,1\"\n" +
                "}";
        collection.insert((DBObject) JSON.parse(json));


        json = "{\n" +
                "\t\"node_id\": \"5\",\n" +
                "\t\"parent_Id\": \"1\",\n" +
                "\t\"child_list_Id\": [\"11\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": null,\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"1,2\"\n" +
                "}";
        collection.insert((DBObject) JSON.parse(json));


        json = "{\n" +
                "\t\"node_id\": \"6\",\n" +
                "\t\"parent_Id\": \"2\",\n" +
                "\t\"child_list_Id\": [\"7\", \"8\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": null,\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"2,1\"\n" +
                "}";

        collection.insert((DBObject) JSON.parse(json));

        json = "{\n" +
                "\t\"node_id\": \"7\",\n" +
                "\t\"parent_Id\": \"6\",\n" +
                "\t\"child_list_Id\": null,\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": null,\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"2,0\"\n" +
                "}";

        collection.insert((DBObject) JSON.parse(json));
        json = "{\n" +
                "\t\"node_id\": \"8\",\n" +
                "\t\"parent_Id\": \"6\",\n" +
                "\t\"child_list_Id\":  [\"10\"],\n" +
                "\t\"dist\": null,\n" +
                "\t\"cluster_head_Id\": null,\n" +
                "\t\"sub_tree_list\": null,\n" +
                "\t\"neighbour_list\": null,\n" +
                "\t\"weight\": null,\n" +
                "\t\"child_weight_list\": null,\n" +
                "\t\"is_Cluster_head\": null,\n" +
                "\t\"state\": \"active\",\n" +
                "\t\"rack_location\": \"2,2\"\n" +
                "}";

        collection.insert((DBObject) JSON.parse(json));


    }


}