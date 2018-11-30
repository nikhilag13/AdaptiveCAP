package org.sjsu.grpcCommunicator;

import java.util.HashMap;

public class NodeIdsList {
    private HashMap<String, String> nodeIdsList = new HashMap<String,String>();

    private int THRESHOLD_S ;

    public  NodeIdsList(){
          nodeIdsList.put("0","localhost:50049");
          nodeIdsList.put("1","localhost:50050");
          nodeIdsList.put("2","localhost:50051");
          nodeIdsList.put("3","localhost:50052");
          nodeIdsList.put("4","localhost:50053");
          nodeIdsList.put("5","localhost:50054");
          nodeIdsList.put("6","localhost:50055");
          nodeIdsList.put("7","localhost:50056");
          nodeIdsList.put("8","localhost:50057");
          nodeIdsList.put("9","localhost:50058");
          nodeIdsList.put("10","localhost:50059");
          nodeIdsList.put("11","localhost:50060");
//         nodeIdsList.put("0","localhost:50050");
//         nodeIdsList.put("1","localhost:50051");
//         nodeIdsList.put("2","localhost:50052");
//         nodeIdsList.put("3","localhost:50053");
//         nodeIdsList.put("4","localhost:50054");
//         nodeIdsList.put("5","localhost:50055");
//         nodeIdsList.put("6","localhost:50056");
//         nodeIdsList.put("7","localhost:50057");
//         nodeIdsList.put("8","localhost:50058");
//         nodeIdsList.put("9","localhost:50059");
//         nodeIdsList.put("10","localhost:50060");
//         nodeIdsList.put("11","localhost:50061");
 //        nodeIdsList.put("12","localhost:50061");
//         nodeIdsList.put("13","localhost:50062");
//         nodeIdsList.put("14","localhost:50063");
//         nodeIdsList.put("15","localhost:50064");
//         nodeIdsList.put("16","localhost:50065");
//         nodeIdsList.put("17","localhost:50066");
//         nodeIdsList.put("18","localhost:50067");
//         nodeIdsList.put("19","localhost:50068");
//         nodeIdsList.put("20","localhost:50069");
//         nodeIdsList.put("21","localhost:50070");

         THRESHOLD_S = 150;
     }

    public int getTHRESHOLD_S() {
        return THRESHOLD_S;
    }

    public HashMap<String, String> getNodeIdsList() {
        return nodeIdsList;
    }
}
