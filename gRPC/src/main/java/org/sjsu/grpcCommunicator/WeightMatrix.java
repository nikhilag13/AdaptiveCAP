package org.sjsu.grpcCommunicator;
import java.util.Random;
import java.util.*;
import java.lang.*;


public class WeightMatrix {


        public int w = 6;
        public int matrix[][] = new int [][]{{0, 3, 5, 7, 2, 4, 5, 7, 9, 1, 1, 2,1,1,1,1,1,1,0,4,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {1,0,5,6,9,2,5,0,7,1,1,2,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {3,5,0,3,0,4,5,3,6,2,3,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {3,6,3,0,3,1,2,3,4,3,5,8,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {4,2,8,10,0,2,5,6,7,2,2,3,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {2,8,1,0,2,0,4,5,6,1,2,3,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {2, 8,1,0,2,5,0,6,7,1,2,3,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {3,4,3,6,2,1,1,0,5,0,0,0,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {5,3,5,5,3,6,2,2,0,1,2,4,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 6, 2, 2, 0, 0, 2, 4,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {5, 3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 4,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,2,2,1,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,2,2,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
                        0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,3,0,1,1,0,1,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,2,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,1,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,1,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,2,0,0,1,0,0,0,1,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,4,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,4,0,0,1,0,0,1,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,1,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,2,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,1,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,2,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,1,1,0,1,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,1,0,0,1,0,0,0,0,0,0},
                {5, 3, 1, 1, 1, 2, 2, 2, 0, 1, 2, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,1,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,1,0,0,0,1,0,0,0,1,0,0}};

        public int getWeight(String nodei) {

            //global matrix
            System.out.println("*******");
            System.out.println(nodei);
            int sum = 0;
            for(int i=0; i< w;i++){
                sum = sum + matrix[Integer.valueOf(nodei)][i];
                sum = sum + matrix[i][Integer.valueOf(nodei)];
            }
            return sum;


        }



}
