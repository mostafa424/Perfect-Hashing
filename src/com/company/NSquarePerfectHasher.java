package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Perfect Hashing in O(N2) space complexity
 * table: Hash table containing values stored after hashing (Immutable)
 * hashEngine: class used for universal hashing
 * collisionRate: How many times it took to recompute the hash function before storing values in the table
 **/
public class NSquarePerfectHasher<T> {
    private ArrayList<T> table;
    private Hasher hashEngine;
    private int collisionRate;
    private int size;

    /**
     * Constructor
     */
    public NSquarePerfectHasher(int keyBitsNum, List<Integer> keys, List<T> values) {
        filterDuplicates(keys, values);
        if(values.size() != 0) size = 1<<(int)Math.ceil(Math.log(values.size())/Math.log(2));
        else return;
        this.table = new ArrayList<>(size * size);
        this.hashEngine = new MatrixHasher((int)(2*Math.log(size)/Math.log(2)), keyBitsNum);
        initTable(size * size);
        perfectHash(keys, values);
    }

    private void filterDuplicates(List<Integer> keys, List<T> values) {
        for(int i = 0; i < keys.size(); i++) {
            for(int j = i+1; j < keys.size(); j++) {
                if(keys.get(i).equals(keys.get(j))) {
                    values.set(i, values.get(j));
                    keys.remove(j);
                    values.remove(j);
                    j--;
                }
            }
        }
    }

    /**
     * Method to initialize table to required size.
     *
     * @param size size to initialize table to.
     */
    private void initTable(int size) {
        for(int i = 0; i < size; i++) {
            this.table.add(null);
        }
    }

    /**
     * used to hash keys and store elements into the hashtable
     * if some keys have the same hash value a hashEngine is generated with a new hash function
     * before storing in the table
    **/
    private void perfectHash(List<Integer> keys, List<T> values) {
        List<Integer> hashTable = getHashTable(keys);
        while(check_for_collision(hashTable)) {
            hashTable = getHashTable(keys);
        }
        for(int i = 0; i < keys.size(); i++){
            table.set(hashTable.get(i), values.get(i));
        }
    }

    /**
     * used to hash keys
     * @param keys :array containing keys to be hashed
     * @return array containing hashed value of keys
     **/
    private List<Integer> getHashTable(List<Integer> keys){
        List<Integer> hashTable = new ArrayList<>(keys.size());
        this.hashEngine.generateFunction();
        for (Integer key : keys) {
            hashTable.add(hashEngine.hash(key));
        }
        return hashTable;
    }

    /**
     * checks if any of the keys have the same hash value before storing them in the table
     * for recomputing the hash function
     * @param hashTable :array containing hashed value of keys
     * @return boolean indicates if there is collision or not
     */
    private boolean check_for_collision(List<Integer> hashTable){
        for(int i = 0; i < hashTable.size(); i++){
            for(int j = i+1; j < hashTable.size(); j++){
                if(hashTable.get(i).equals(hashTable.get(j))){
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

    public T get(int key) {
        return this.table.get(this.hashEngine.hash(key));
    }

    public void update(int key, T val) {
        this.table.set(this.hashEngine.hash(key), val);
    }

    public int getNumOfCells() {
        return this.size;
    }
}
