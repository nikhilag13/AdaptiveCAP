package org.sjsu.grpcCommunicator;

import com.mongodb.*;
import org.apache.http.util.ExceptionUtils;
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
    List<String> sub_tree_list;
    List<String> neighbour_list;
    int weight;
    int size;
    int child_request_counter;
    int initial_node_child_length ;
    String best_node_id;
    int best_node_hop_count;
    String best_node_cluster_head_Id;
    List<String>  neighbor_ID;
    public HashSet<String> neighbour_Hello_Array; //Phase 2

    String shift_Node_Id ;
    int shift_Node_Sum ;
    String shift_Node_Cluster ;

    int is_Cluster_head ;
    String state;
    {
        System.out.println("Node Object Created ");
    }

    NodeIdsList nodeIdsList =new NodeIdsList();

    MongoClient mongo = new MongoClient("localhost", 27017);
    DB db = mongo.getDB("cmpe295Project");

    // get a single collection
    DBCollection collection = db.getCollection("spanningtree");
    WeightMatrix weigthMatrix = new WeightMatrix();
    CommunicatorClient client = new CommunicatorClient();

    public Node(){

    }

    public Node(int node_id){
        this.id = String.valueOf(node_id);

        try {

            logger.info("################################################");
            logger.info("Constructor initialization for Node: "+ this.id);
            logger.info("################################################");

            BasicDBObject query = new BasicDBObject();
            query.put("node_id", this.id);

            DBObject document = collection.findOne(query);
            logger.info("Constructor "+ this.id);

            ip_address =nodeIdsList.getNodeIdsList().get(node_id);
            parent_Id = (String) document.get("parent_Id");

            logger.info("its parent id : "+ parent_Id);
            child_list_Id = new ArrayList<String>();
            BasicDBList list = (BasicDBList)document.get("child_list_Id");

            if(list!=null) {
                for (Object el : list) {
                    child_list_Id.add((String) el);
                }
            }
            if((document.get("dist")!=null)){
                dist =(int) document.get("dist");
            }

            if((document.get("cluster_head_Id")!=null)){
                cluster_head_Id =(String) document.get("cluster_head_Id");
            }

            hop_count=0;

            rack_location= (String)document.get("rack_location");

            sub_tree_list = new ArrayList<String>();

            BasicDBList list1 = (BasicDBList)document.get("sub_tree_list");
            if(list1!=null) {
                for (Object el : list1) {
                    sub_tree_list.add((String) el);
                }
            }

            neighbour_list = new ArrayList<String>();

            BasicDBList list2 = (BasicDBList)document.get("sub_tree_list");
            if(list2!=null) {
                for (Object el : list2) {
                    neighbour_list.add((String) el);
                }
            }

            weight = weigthMatrix.getWeight(id);
            size= weight;
            child_request_counter=0;
            initial_node_child_length=0;
            best_node_id=id;
            best_node_hop_count = hop_count;
            best_node_cluster_head_Id=cluster_head_Id;

            neighbor_ID = new ArrayList<String>();
            logger.info("constructor get_Neighbors "+ this.neighbor_ID);
            get_Neighbors();

            neighbour_Hello_Array = new HashSet<>(); //Phase 2

            initial_node_child_length= child_list_Id.size();
            shift_Node_Sum=0;
            shift_Node_Cluster="";
            shift_Node_Id=null;

            if((document.get("is_Cluster_head")!=null)){
                is_Cluster_head =(int) document.get("is_Cluster_head");
            }

            state=(String) document.get("state");
            logger.info(" Starting Phase One Clustering in constructor "+ this.id);


            // starting phase 1 and calling server "serve() " function from node constructor
            start_phase_one_clustering();
            logger.info("Node: "+this.id+" - Calling Server ");
            final CommunicatorServer server = new CommunicatorServer();
            server.serve(this);


        }catch (Exception e){
           e.printStackTrace();
            logger.error("Error:::",e);
        }


    }

    //    public void sendSizeToParent() {
//            logger.info("size sent to parent node");
//    }

    public void get_Neighbors(){
        logger.info(" get_Neighbors "+ this.id);
        String rack_row= rack_location.split(",")[0];
        String rack_column= rack_location.split(",")[1];
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
                  this.neighbor_ID.add((String) document.get("node_id"));
                else
                    logger.info("Node: "+id+"- No node with rackLocation:"+el+" found!");

            }catch(Exception e){
                System.out.println(e);
            }
        }

    }



    public int getChildWeight(){
        int child_weight=0;
         if (this.child_list_Id!=null){
             for(String c: this.child_list_Id){
                 child_weight = child_weight+ weigthMatrix.getWeight(c);
             }
        }
        return child_weight;
    }


  public void send_size_to_parent(){
        if(this.parent_Id!=null){
            client.startStageOneCluster(this,nodeIdsList.getNodeIdsList().get(this.parent_Id));
        }else{
            logger.info("Node: "+this.id+"- Setting myself as clusterhead as no parent found! "+id);
            this.is_Cluster_head=1;
            this.cluster_head_Id= this.id;
            this.state = "free";
            BasicDBObject query = new BasicDBObject();
            query.put("node_id", this.id);
            try{
                logger.info("Node: "+this.id+"- Updating DB with size,hopcount variables "+id+" "+this.size);
                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("is_Cluster_head", this.is_Cluster_head);
                newDocument.put("cluster_head_Id", this.cluster_head_Id);
                newDocument.put("parent_Id", null);
                newDocument.put("size", this.size);
                newDocument.put("hop_count", 0);
                newDocument.put("state", this.state);



                BasicDBObject updateObject = new BasicDBObject();
                updateObject.put("$set", newDocument);

                collection.update(query, updateObject);

                logger.info("Node: "+this.id+"- Successfully DB with size,hopcount variables");
            }catch(Exception e){
                logger.error("Some Error occurred in sendSizeToParent()");
               System.out.println(e);
            }
            client.send_Cluster(this);
        }
  }


    public void propogate_Cluster_head_Info(String cluster_name, int hop_count){
        if(this.child_list_Id!=null && this.child_list_Id.size()!=0) {
            logger.info("propogate_Cluster_head_Info "+hop_count+1);
          client.propogate_Cluster_head_Info(this,cluster_name,hop_count+1);
        }

    }


    public void start_phase_one_clustering(){
        System.out.println("Node: "+this.id+"- Starting Phase One Clustering ");
        logger.info("Node: "+this.id+"- Starting Phase One Clustering ");
//        logger.info("inside start phase one clustering in node"+ this.id + "and its children ids: " +this.child_list_Id);
        if(this.child_list_Id==null || this.child_list_Id.size()==0){
            logger.info("Node: "+this.id+" - Calling phaseOneClusterStart with parentId: "+this.parent_Id);
            client.startStageOneCluster(this,nodeIdsList.getNodeIdsList().get(this.parent_Id));
//            System.out.println("Node: "+this.id+"- Sent size() message to parent: "+this.parent_Id);
            logger.info("Node: "+this.id+"- Sent size() message to parent: "+this.parent_Id);
        }else{
            logger.info("Node: "+this.id+"- I am a parent not leaf "+ this.id);
        }

    }


    public void send_shift_node_request(String best_node_cluster_head_Id){
        logger.info("Node  "+ this.id + " got shift node request");
        if(this.is_Cluster_head!=1){
            client.send_Shift_Node_Request(this,best_node_cluster_head_Id,nodeIdsList.getNodeIdsList().get(this.cluster_head_Id));
        }
    }


    public void propogate_jam_to_children(String jamID){
        logger.info("Node: "+this.id+"- Adding childIps for propagating jam signal " );
        if(this.child_list_Id==null || this.child_list_Id.size()==0){
            logger.info("Node: "+this.id+"- Leaf node. NOT propagating Jam signal anymore ");
            return;
        }

        List<String>childIPs = new ArrayList<String>();
        for(String childId : this.child_list_Id)
            childIPs.add(nodeIdsList.getNodeIdsList().get(childId));
        logger.info(childIPs);
        client.propagate_Jam_To_Children(childIPs,jamID,this.id);
    }

    public void propogate_wake_up(){
        if(this.child_list_Id!=null && this.child_list_Id.size()!=0){
            logger.info("Node: "+this.id+"- Propagating wakeup to children.  ") ;
            List<String>childIPs = new ArrayList<String>();
            for(String childId : this.child_list_Id)
                childIPs.add(nodeIdsList.getNodeIdsList().get(childId));
            client.propagate_WakeUp(childIPs,this.id);
        }else{
            logger.info("Node: "+this.id+"- No children found! Stopping wakeup propagation. ");
        }
    }

    public void update_internal_variables_and_send_join(String best_node_id, String best_node_cluster_head_id, int new_hop_count){
        logger.info("Node: {} - Updating parent,clusterhead and hopcount from "+this.id +" "+this.parent_Id+" "+ this.cluster_head_Id +" "+this.hop_count+" "+this.best_node_id +" "+best_node_cluster_head_id+" "+ String.valueOf(this.best_node_hop_count));
        this.parent_Id = this.best_node_id;
        this.cluster_head_Id = best_node_cluster_head_id;
        this.hop_count= this.best_node_hop_count;
        BasicDBObject query = new BasicDBObject();
        query.put("node_id", this.id);
        try{

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("cluster_head_Id", this.cluster_head_Id);
            newDocument.put("parent_Id", this.parent_Id);
            newDocument.put("hop_count", this.hop_count);

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);

            collection.update(query, updateObject);

        }catch(Exception e){
            logger.error("Some error occurred while updating db in update_internal_variables_and_send_join()");
            System.out.println(e);
        }
        client.join_New_Parent(this.id,this.size,nodeIdsList.getNodeIdsList().get(best_node_id));

    }

    public void propagate_new_cluster_head_to_children(){
        List<String>childIPs = new ArrayList<String>();
        for(String childId : this.child_list_Id)
            childIPs.add(nodeIdsList.getNodeIdsList().get(childId));
        client.propagate_New_Cluster_Head_To_Children(childIPs,this.id, this.cluster_head_Id);

    }

    public void inform_parent_about_new_size(int size_increment){
        if(this.parent_Id!=null){
            client.inform_Parent_About_New_Size(size_increment,this.id,nodeIdsList.getNodeIdsList().get(this.parent_Id));
        }
    }

    public void say_bye_to_parent(){
        client.remove_Child_Id_From_Parent(this.id, nodeIdsList.getNodeIdsList().get(this.parent_Id));
        client.inform_Parent_About_New_Size(this.size*(-1),this.id,nodeIdsList.getNodeIdsList().get(this.parent_Id));
    }

    public void send_shift_complete_to_both_cluster_heads(String old_cluster_head_id, String new_cluster_head_id){
        client.sendShiftCompleteToBothClusterHeads(nodeIdsList.getNodeIdsList().get(old_cluster_head_id),nodeIdsList.getNodeIdsList().get(new_cluster_head_id), this.id);
    }


    public void start_phase2_clustering(){
        this.best_node_hop_count = this.hop_count;
        logger.info("Node: "+this.id+" - hopcount before Phase 2 clustering:  "+this.hop_count);

    for (String i : this.neighbor_ID) {
        logger.info("i is "+ i +", id is "+ this.id);
        if(i != null) {
            if (i.equals(this.id))
                continue;
            logger.info("i is "+ i +", cluster_head_Id: "+  this.cluster_head_Id);
            client.sendHello(this.id, i, nodeIdsList.getNodeIdsList().get(i), this.cluster_head_Id, this.hop_count, this.state);
        }
    }
    }

    public String get_ip_from_id(String Id){
        String ip ="";

        try{
            ip = nodeIdsList.getNodeIdsList().get(Id);
        }catch (Exception e){
            logger.error("Error occurred while finding IP of "+Id);

        }
        return ip;
    }

    public void send_jam_signal(){

        logger.info("Node " + this.id + " got jam signal from server");
        List<String> childIpList = new ArrayList<String>();
        if(this.child_list_Id!=null && this.child_list_Id.size()!=0){
            for(String childId : this.child_list_Id){
                childIpList.add(nodeIdsList.getNodeIdsList().get(childId));
            }
         client.send_Jam_Signal(childIpList,this.cluster_head_Id);

        }
    }

    public void send_shift_cluster_request(){
        logger.info("Node: "+this.id+" - Clusterhead sending ShiftClusterRequest to clusterheadId: "+ this.shift_Node_Cluster);
        String shift_node_cluster_ip = this.get_ip_from_id(this.shift_Node_Cluster);
        client.send_Shift_Cluster_Request(this.cluster_head_Id, this.shift_Node_Id, this.shift_Node_Sum, shift_node_cluster_ip);
    }

    public void accept(String sender_cluster_head_id){
        String sender_cluster_head_ip = this.get_ip_from_id(sender_cluster_head_id);
        client.send_Accept(this.id,sender_cluster_head_ip);
    }

    public void reject(String send_cluster_head_id){
      String  sender_cluster_head_ip  = this.get_ip_from_id(send_cluster_head_id);
      client.send_Reject(this.id, sender_cluster_head_ip);
    }

    public void send_shift_start(){
        client.sendShiftStart(this.id, this.shift_Node_Id, this.get_ip_from_id(this.shift_Node_Id));
    }

    public void send_shift_finished(){
         client.sendShiftFinished(this.id, this.get_ip_from_id(this.shift_Node_Cluster));
    }

    public void send_wakeup(){
        List<String> childIpList = new ArrayList<String>();
        if(this.child_list_Id!=null && this.child_list_Id.size()!=0){
            for(String childId : this.child_list_Id){
                childIpList.add(nodeIdsList.getNodeIdsList().get(childId));
            }
            client.sendWakeUp(childIpList,this.id);
        }
    }

    public boolean check_energy(){
        int initial_energy = 0;
        int final_energy = 0;
        BasicDBObject query = new BasicDBObject();
        query.put("node_id", this.shift_Node_Id);

        DBObject document = collection.findOne(query);

        int shift_node_initial_hopcount = (int) document.get("hop_count");
        int shift_node_final_hopcount = this.best_node_hop_count+1;
        int cluster_head_to_cluster_head_hop_count =1;

        Queue<String> children_list1 = new LinkedList<String>();
        Queue<String> children_list2 = new LinkedList<String>();

        for(String i: this.child_list_Id)
            children_list1.add(i);

        query = new BasicDBObject();
        query.put("node_id", this.shift_Node_Cluster);

        DBObject document2 = collection.findOne(query);

        BasicDBList list = (BasicDBList)document2.get("child_list_Id");
        if(list!=null) {
            for (Object el : list) {
                children_list2.add((String) el);
            }
        }

        try {
            while (children_list1.size() != 0) {
                String childId = children_list1.poll();
                query = new BasicDBObject();
                query.put("node_id", childId);

                DBObject document3 = collection.findOne(query);
                int weight = weigthMatrix.matrix[Integer.parseInt(childId)][Integer.parseInt(this.shift_Node_Id)];
                int hops = (int) document.get("hop_count");
                initial_energy = initial_energy + (weight * (hops + shift_node_initial_hopcount));
                final_energy = final_energy + (weight * (hops + shift_node_final_hopcount + cluster_head_to_cluster_head_hop_count));
                BasicDBList list2 = (BasicDBList) document3.get("child_list_Id");
                if (list2 != null) {
                    for (Object el : list2) {
                        children_list1.add((String) el);
                    }
                }

            }
        }catch (Exception e){
            logger.error(e);
        }

        logger.info("childList1 empty");

        try {
            while (children_list2.size() != 0) {
                String childIdOtherCluster = children_list2.poll();
                query = new BasicDBObject();
                query.put("node_id", childIdOtherCluster);

                DBObject document3 = collection.findOne(query);
                int weight = weigthMatrix.matrix[Integer.parseInt(childIdOtherCluster)][Integer.parseInt(this.shift_Node_Id)];
                int hops = (int) document.get("hop_count");
                initial_energy = initial_energy + (weight * (hops + shift_node_initial_hopcount+cluster_head_to_cluster_head_hop_count));
                final_energy = final_energy + (weight * (hops + shift_node_final_hopcount ));
                BasicDBList list3 = (BasicDBList) document3.get("child_list_Id");
                if (list3 != null) {
                    for (Object el : list3) {
                        children_list2.add((String) el);
                    }
                }

            }
        }catch (Exception e){
            logger.error(e);
        }

        logger.info("Node: "+this.id+" - Initial Energy: "+initial_energy);
        logger.info("Node: "+this.id+" - Final Energy: "+final_energy);
        return initial_energy > final_energy;

    }



    public void calculate_energy_drain(){
        if(this.is_Cluster_head!=1){

        }else{

            try{

            BasicDBObject query = new BasicDBObject();
            query.put("cluster_head_Id", this.id);
            List<DBObject> myList = null;
            DBCursor myCursor = collection.find(query);
            myList = myCursor.toArray();
            int energy =0 ;

            List<String> all_nodes = new ArrayList<String>();
            for(DBObject obj : myList){
                all_nodes.add((String) obj.get("node_id"));
            }

            for(DBObject obj : myList) {
               int hops =  (int) obj.get("hop_count");
               String this_nodeid = (String) obj.get("node_id");
               int weight =0;
               for(String n : all_nodes){
                   weight =weight+ weigthMatrix.matrix[Integer.parseInt(this_nodeid)][Integer.parseInt(n)];
               }
                logger.info("Node: " + this.id + "  weight of node:{} is {} "+ weight);
                energy += weight*hops;
                logger.info("Node: " + this.id + " energy: "+energy);
            }

            query = new BasicDBObject();
            query.put("node_id", this.id);

                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("init_energy", energy);
                BasicDBObject updateObject = new BasicDBObject();
                updateObject.put("$set", newDocument);

                collection.update(query, updateObject);

            }catch(Exception e){
                logger.error("Error in calculateClusterEnergy");
                System.out.println(e);
            }
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_Id() {
        return parent_Id;
    }

    public void setParent_Id(String parent_Id) {
        this.parent_Id = parent_Id;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public List<String> getChild_list_Id() {
        return child_list_Id;
    }

    public String getIPfromId(String id){
        LinkedHashMap<String, String> list =  nodeIdsList.getNodeIdsList();
        return list.get(id);
    }


    public void setChild_list_Id(List<String> child_list_Id) {
        this.child_list_Id = child_list_Id;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public String getCluster_head_Id() {
        return cluster_head_Id;
    }

    public void setCluster_head_Id(String cluster_head_Id) {
        this.cluster_head_Id = cluster_head_Id;
    }

    public int getHop_count() {
        return hop_count;
    }

    public void setHop_count(int hop_count) {
        this.hop_count = hop_count;
    }

    public String getRack_location() {
        return rack_location;
    }

    public void setRack_location(String rack_location) {
        this.rack_location = rack_location;
    }

    public List<String> getSub_tree_list() {
        return sub_tree_list;
    }

    public void setSub_tree_list(List<String> sub_tree_list) {
        this.sub_tree_list = sub_tree_list;
    }

    public List<String> getNeighbour_list() {
        return neighbour_list;
    }

    public void setNeighbour_list(List<String> neighbour_list) {
        this.neighbour_list = neighbour_list;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getChild_request_counter() {
        return child_request_counter;
    }

    public void setChild_request_counter(int child_request_counter) {
        this.child_request_counter = child_request_counter;
    }

    public int getInitial_node_child_length() {
        return initial_node_child_length;
    }

    public void setInitial_node_child_length(int initial_node_child_length) {
        this.initial_node_child_length = initial_node_child_length;
    }

    public String getBest_node_id() {
        return best_node_id;
    }

    public void setBest_node_id(String best_node_id) {
        this.best_node_id = best_node_id;
    }

    public int getBest_node_hop_count() {
        return best_node_hop_count;
    }

    public void setBest_node_hop_count(int best_node_hop_count) {
        this.best_node_hop_count = best_node_hop_count;
    }

    public String getBest_node_cluster_head_Id() {
        return best_node_cluster_head_Id;
    }

    public void setBest_node_cluster_head_Id(String best_node_cluster_head_Id) {
        this.best_node_cluster_head_Id = best_node_cluster_head_Id;
    }

    public List<String> getNeighbor_ID() {
        return neighbor_ID;
    }

    public void setNeighbor_ID(List<String> neighbor_ID) {
        this.neighbor_ID = neighbor_ID;
    }

    public String getShift_Node_Id() {
        return shift_Node_Id;
    }

    public void setShift_Node_Id(String shift_Node_Id) {
        this.shift_Node_Id = shift_Node_Id;
    }

    public int getShift_Node_Sum() {
        return shift_Node_Sum;
    }

    public void setShift_Node_Sum(int shift_Node_Sum) {
        this.shift_Node_Sum = shift_Node_Sum;
    }

    public String getShift_Node_Cluster() {
        return shift_Node_Cluster;
    }

    public void setShift_Node_Cluster(String shift_Node_Cluster) {
        this.shift_Node_Cluster = shift_Node_Cluster;
    }

    public int getIs_Cluster_head() {
        return is_Cluster_head;
    }

    public void setIs_Cluster_head(int is_Cluster_head) {
        this.is_Cluster_head = is_Cluster_head;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public HashSet<String> getNeighbour_Hello_Array() {
        return neighbour_Hello_Array;
    }

    public void setNeighbour_Hello_Array(HashSet<String> neighbour_Hello_Array) {
        this.neighbour_Hello_Array = neighbour_Hello_Array;
    }

}


