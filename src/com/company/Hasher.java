package com.company;

public interface Hasher {
    void generateFunction();
    long hash(long key);
}
