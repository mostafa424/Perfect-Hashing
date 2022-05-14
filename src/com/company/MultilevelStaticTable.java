package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Perfect Hashing in O(N) space complexity
 * tables: Hash table linking to smaller O(N^2) tables
 * keyBitsNum: number of bits a key is made up of.
 * size: number of cells in first level hash table.
 * numOfCells: total number of cells in second level hash tables.
 * firstLevelRehashes: number of times the first level hash function was recomputed. (Done if numOfCells >= 3*N)
 * hashEngine: class used for universal hashing
 **/
public class MultilevelStaticTable<V> {
    private final List<NSquarePerfectHasher<V>> tables;
    private final int keyBitsNum;
    private final int size;
    private int numOfCells;
    private int firstLevelRehashes;
    private final Hasher hashEngine;

    public MultilevelStaticTable(int keyBitsNum, List<Integer> keys, List<V> values) {
        filterDuplicates(keys, values);
        this.size = 1<<(int)Math.ceil(Math.log(keys.size())/Math.log(2));
        this.tables = new ArrayList<>(size);
        this.keyBitsNum = keyBitsNum;
        this.hashEngine = new MatrixHasher((int)(Math.log(size)/Math.log(2)), keyBitsNum);
        this.insertAll(keys, values);
    }

    /**
     * Method to remove duplicate keys and values from provided lists.
     * If a duplicate key is found with different value updates earlier value to later one.
     *
     * @param keys <code>List</code> of <code>Integer</code> containing keys
     * @param values <code>List</code> of <code>Integer</code> containing values
     */
    private void filterDuplicates(List<Integer> keys, List<V> values) {
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
     * Method to insert a set of keys and values into the hash table,
     * recomputes the hash function for the first level iff
     * the calculated number of cells to be used in second level is
     * equal to or greater than 3 times the number of keys to be inserted.
     *
     * @param keys <code>List</code> of <code>Integer</code> containing keys
     * @param values <code>List</code> of <code>Integer</code> containing values
     */
    private void insertAll(List<Integer> keys, List<V> values) {
        int[] hashes = new int[keys.size()];
        int[] hashCount = new int[this.size];
        this.hashEngine.generateFunction();
        for(int i = 0; i < keys.size(); i++) {
            hashes[i] = this.hashEngine.hash(keys.get(i));
            hashCount[hashes[i]]++;
        }
        int factorSum = 0;
        for(int i = 0; i < this.size; i++) {
            if(hashCount[i] == 0) continue;
            int size = 1<<(int)Math.ceil(Math.log(hashCount[i])/Math.log(2));
            factorSum += size * size;
        }
        if(factorSum >= 3 * keys.size()) {
            this.firstLevelRehashes++;
            insertAll(keys, values);
        } else {
            this.numOfCells = factorSum;
            List<List<Integer>> partitionedKeys = new ArrayList<>();
            List<List<V>> partitionedValues = new ArrayList<>();
            for(int i = 0; i < this.size; i++) {
                partitionedKeys.add(new ArrayList<>());
                partitionedValues.add(new ArrayList<>());
            }
            for(int i = 0; i < keys.size(); i++) {
                partitionedKeys.get(hashes[i]).add(keys.get(i));
                partitionedValues.get(hashes[i]).add(values.get(i));
            }
            for(int i = 0; i < this.size; i++) {
                this.tables.add(new NSquarePerfectHasher<V>(this.keyBitsNum,
                        partitionedKeys.get(i),
                        partitionedValues.get(i)));
            }
        }
    }

    /**
     * Method to get an object stored in the hash table.
     *
     * @param key the key with which to access the object.
     * @return object mapped to hash map using key (null if key was not previously used).
     */
    public V get(int key) {
        return this.tables.get(this.hashEngine.hash(key)).get(key);
    }

    /**
     * Method to update an object associated with a previously inserted key.
     * if key was not previously inserted, does nothing.
     *
     * @param key the key with which to access the object.
     * @param val the new value to associate with key
     */
    public void update(int key, V val) {
        this.tables.get(this.hashEngine.hash(key)).update(key, val);
    }

    /**
     * Getter for number of times the first level hash function was re-computed.
     *
     * @return <code>int</code> denoting number of re-computations.
     */
    public int getRehashesNum() {
        return this.firstLevelRehashes;
    }

    /**
     * Getter for number of times the second level hash functions were re-computed.
     *
     * @return <code>int</code> denoting number of re-computations.
     */
    public List<Integer> getInnerRehashesNum() {
        List<Integer> res = new ArrayList<>(this.tables.size());
        for(var table: this.tables) {
            res.add(table.getCollisionRate());
        }
        return res;
    }

    /**
     * Method to return number of cells used by first level table.
     *
     * @return <code>int</code> value denoting number of cells used.
     */
    public int getFirstLevelCells() {
        return this.size;
    }

    /**
     * Method to return number of cells used by second level tables.
     *
     * @return <code>int</code> value denoting number of cells used.
     */
    public int getSecondLevelCells() {
        return this.numOfCells;
    }
}
