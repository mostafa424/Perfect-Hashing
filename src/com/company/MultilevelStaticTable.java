package com.company;

import java.util.ArrayList;
import java.util.List;

public class MultilevelStaticTable<V> {
    private final List<NSquarePerfectHasher<V>> tables;
    private final int keyBitsNum;
    private int firstLevelRehashes;
    private final Hasher hashEngine;

    public MultilevelStaticTable(int keyBitsNum, List<Integer> keys, List<V> values) {
        this.tables = new ArrayList<>(1<<(int)Math.ceil(Math.log(keys.size())/Math.log(2)));
        this.keyBitsNum = keyBitsNum;
        this.hashEngine = new MatrixHasher((int)Math.ceil(Math.log(keys.size())/Math.log(2)), keyBitsNum);
        this.insertAll(keys, values);
    }

    private void insertAll(List<Integer> keys, List<V> values) {
        int[] hashes = new int[keys.size()];
        int[] hashCount = new int[keys.size()];
        this.hashEngine.generateFunction();
        for(int i = 0; i < keys.size(); i++) {
            hashes[i] = this.hashEngine.hash(keys.get(i));
            hashCount[hashes[i]]++;
        }
        int factorSum = 0;
        for(int i = 0; i < keys.size(); i++) {
            if(hashCount[i] == 0) continue;
            int size = 1<<(int)Math.ceil(Math.log(hashCount[i])/Math.log(2));
            factorSum += size * size;
        }
        if(factorSum >= 4 * keys.size()) {
            this.firstLevelRehashes++;
            insertAll(keys, values);
        } else {
            List<List<Integer>> partitionedKeys = new ArrayList<>();
            List<List<V>> partitionedValues = new ArrayList<>();
            for(int i = 0; i < keys.size(); i++) {
                partitionedKeys.add(new ArrayList<>());
                partitionedValues.add(new ArrayList<>());
            }
            for(int i = 0; i < keys.size(); i++) {
                partitionedKeys.get(hashes[i]).add(keys.get(i));
                partitionedValues.get(hashes[i]).add(values.get(i));
            }
            for(int i = 0; i < keys.size(); i++) {
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
}
