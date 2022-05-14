package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> values = new ArrayList<>(List.of("bop", "beep", "meep", "mop", "cube", "hen", "kappa", "pogchamp"));
        List<Integer> keys = new ArrayList<>(List.of(2, 15, 21, 39, 815, 1, 210, 399));
        MultilevelStaticTable<String> hashTable = new MultilevelStaticTable<>(10, keys, values);
    }
}
