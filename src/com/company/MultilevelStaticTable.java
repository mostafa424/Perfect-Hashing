package com.company;

import java.util.ArrayList;
import java.util.List;

public class MultilevelStaticTable<V> {
    private final StaticTable<V>[] tables;
    private final byte keyBitsNum;
    private byte firstLevelRehashes;
    private final Hasher hashEngine;

    public MultilevelStaticTable(long tableSize, byte keyBitsNum, int[] keys, V[] values) {
        this.tables = new StaticTable<V>[1<<(int)Math.ceil(Math.log(tableSize))];
        this.keyBitsNum = keyBitsNum;
        this.hashEngine = new MatrixHasher((byte) Math.log(tables.length), keyBitsNum);
        this.insertAll(keys, values);
    }

    private void insertAll(int[] keys, V[] values) {
        int[] hashes = new int[keys.length];
        byte[] hashCount = new byte[keys.length];
        this.hashEngine.generateFunction();
        for(int i = 0; i < keys.length; i++) {
            hashes[i] = this.hashEngine.hash(keys[i]);
            hashCount[hashes[i]]++;
        }
        int factorSum = 0;
        for(int i = 0; i < keys.length; i++) {
            factorSum += hashCount[i] * hashCount[i];
        }
        if(factorSum >= 4 * keys.length) {
            this.firstLevelRehashes++;
            insertAll(keys, values);
        } else {
            List<List<Integer>> partitionedKeys = new ArrayList<>();
            List<List<V>> partitionedValues = new ArrayList<>();
            for(int i = 0; i < keys.length; i++) {
                partitionedKeys.add(new ArrayList<>());
                partitionedValues.add(new ArrayList<>());
            }
            for(int i = 0; i < keys.length; i++) {
                partitionedKeys.get(hashes[i]).add(keys[i]);
                partitionedValues.get(hashes[i]).add(values[i]);
            }
            for(int i = 0; i < keys.length; i++) {
                this.tables[i] = new StaticTable<V>(hashCount[i],
                        this.keyBitsNum,
                        partitionedKeys.get(i).toArray(),
                        partitionedValues.get(i).toArray());
            }
        }
    }

    public V get(int key) {
        return this.tables[this.hashEngine.hash(key)].get(key);
    }

    public void update(int key, V val) {
        this.tables[this.hashEngine.hash(key)].update(key, val);
    }

    public byte getRehashesNum() {
        return this.firstLevelRehashes;
    }
}
