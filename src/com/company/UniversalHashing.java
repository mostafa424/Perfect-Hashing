package com.company;

import java.util.ArrayList;
import java.util.List;

public class UniversalHashing {
    /**
     * used to construct the Universal Hashset
     * @param u size of key
     * @param b M=2^b where M=size of table
     * @param x key whose value is to be evaluated
     * @return
     */
    public int getUniversalHashing(int u,int b,int x){
        // construct H matrix randomly
        int[][]h=new int[b][u];
        for(int i=0;i<b;i++){
            for(int j=0;j<u;j++){
                    h[i][j]=(int)Math.round(Math.random());
            }
        }
        // convert key X to it's binary in an array
        List<Integer> x_in_bits=convertToBits(x);
        //get position of ones in Vector X
        List<Integer>ones_in_vectorX=getOnesInVectorX(x_in_bits);
        //compute HX
        int[]hx=computeHashValueInBits(h,ones_in_vectorX);
        // return hash value of key X in decimal form
        return convert_to_decimal(hx);
    }
    /**
     * used to convert a number to bits
     **/
    private List<Integer> convertToBits(int n){
        List<Integer> res=new ArrayList<>();
        while(n!=0){
            res.add(n%2);
            n/=2;
        }
        return res;
    }
    /**
     * used to convert a binary number to decimal
     * @param x number in binary in form of vector
      **/
    private int convert_to_decimal(int[]x){
        int res=0;
        for(int i=x.length-1;i>=0;i--){
            res+=x[i]*Math.pow(2,x.length-1-i);
        }
        return res;
    }

    /**
     * used to determine positions of ones in vector X
     * @param x vector X
     * @return List containing positions of ones
     */
    private List<Integer> getOnesInVectorX(List<Integer> x){
        List<Integer>res=new ArrayList<>();
        for(int i=0;i<x.size();i++){
            if(x.get(i)==1){
                res.add(i);
            }
        }
        return res;
    }

    /**
     * used to evaluate hx by adding columns from h whose positions in vector x is one
     * ex:
     *      x=[1010] means this function will add 1st and 3rd column in H
     * @param h H matrix
     * @param ones_in_vector_x positions of ones in vector x
     * @return HX vector
     */
    private int[] computeHashValueInBits(int[][]h,List<Integer>ones_in_vector_x){
        int[]res=new int[h.length];
        if(!ones_in_vector_x.isEmpty()){
            for(int i=0;i<ones_in_vector_x.size();i++){
                for(int j=0;j<h.length;j++){
                   res[j]+=h[j][ones_in_vector_x.get(i)];
                }
            }
        }
        return res;
    }
}
