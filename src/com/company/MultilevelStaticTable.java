package com.company;

import java.util.ArrayList;
import java.util.List;

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

    public V get(int key) {
        return this.tables.get(this.hashEngine.hash(key)).get(key);
    }

    public void update(int key, V val) {
        this.tables.get(this.hashEngine.hash(key)).update(key, val);
    }

    public int getRehashesNum() {
        return this.firstLevelRehashes;
    }

    public List<Integer> getInnerRehashesNum() {
        List<Integer> res = new ArrayList<>(this.tables.size());
        for(var table: this.tables) {
            res.add(table.getCollisionRate());
        }
        return res;
    }

    public int getFirstLevelCells() {
        return this.size;
    }

    public int getSecondLevelCells() {
        return this.numOfCells;
    }
}
