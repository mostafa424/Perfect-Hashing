package com.company;
/**
 * Perfect Hashing in O(N2) space complexity
 * table: Hash table  containg values stored after hashing (Immutable)
 * keys: array of keys to be hashed(assume keys are 8-bits long)
 * values: array of values to be stored
 * hashEngine: class used for universal hashing
 * collisionRate: How many times it took to recompute the hashfunction before storing values in the table
 **/
public class NSquarePerfectHasher<T> implements PerfectHasher{
    private  Object []table;
    private  Object []values;
    private  int[]keys;
    private Hasher hashEngine;

    private int collisionRate;



    /**
     * Constructor
     */
    public NSquarePerfectHasher(Object[] values,int[]keys) {
        this.table = new Object [(int) Math.pow(values.length, 2)];
        this.values=values;
        this.keys=keys;
        this.hashEngine = new MatrixHasher((byte)(Math.log(values.length)/Math.log(2)),(byte) 8);
        perfectHash();
    }
    /**
    * used to hash keys and store elements into the hashtable
     * if some keys have the same hashvalue a hashEngine is generated with a new hash function
     * before storing in the table
    **/
    @Override
    public void perfectHash() {
        int[]hashTable=getHashTable(keys);
        while(check_for_collision(hashTable)){
            hashEngine=new MatrixHasher((byte)(Math.log(values.length)/Math.log(2)),(byte) 8);
            hashTable=getHashTable(keys);
        }
        int k=0;
        for(int i : keys){
            table[hashTable[i]]=values[k++];
        }
    }
    /**used to hash keys
     * @param keys :array containing keys to be hashed
     * @return array containg hashed value of keys
     **/
    private int[] getHashTable(int[] keys){
        int[]hashTable=new int[keys.length];
        for(int i=0;i< keys.length;i++){
            hashTable[i]=hashEngine.hash(keys[i]);
        }
        return hashTable;
    }
    /**checks if any of the keys have the same hashvalue before storing them in the table
     * for recomputing the hash function
     * @param hashtable :array containg hashed value of keys
     * @return boolean indicates if there is collision or not
     */
    private boolean check_for_collision(int[]hashtable){
        for(int i=0;i<hashtable.length;i++){
            for(int j=i+1;j<hashtable.length;j++){
                if(hashtable[i]==hashtable[j]){
                    collisionRate++;
                    return true;
                }
            }
        }
        return false;
    }
    public int getCollisionRate() {
        return collisionRate;
    }
}
