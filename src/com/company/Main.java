package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Press 1 to test O(N^2) Hash Table,2 to test O(N) Hash Table, 3 to exit : ");
        int in = sc.nextInt();
        switch (in) {
            case 1 -> {
                System.out.println("You are testing O(N^2) Hash Table");
                System.out.print("Enter number of keys to randomly generate (Duplicates generated will be removed): ");
                int keyNum = sc.nextInt();
                List<Integer> testKeys = new ArrayList<>();
                List<Integer> testValues = new ArrayList<>();
                Random random = new Random();
                for (int i = 0; i < keyNum; i++) {
                    testKeys.add(random.nextInt());
                    testValues.add(random.nextInt());
                }
                NSquarePerfectHasher<Integer> test = new NSquarePerfectHasher<>(32, testKeys, testValues);
                while (true) {
                    System.out.println("Select the operation you want to perform by pressing the respective number: ");
                    System.out.println("1:get collisions");
                    System.out.println("2:get number of cells");
                    System.out.println("3:exit");
                    int option = sc.nextInt();
                    switch (option) {
                        case 1 -> System.out.println(test.getCollisionRate());
                        case 2 -> System.out.println(test.getNumOfCells());
                    }
                    if (option == 3) {
                        System.out.println("Exiting....");
                        break;
                    }
                }
            }
            case 2 -> {
                System.out.println("You are testing O(N) Hash Table");
                System.out.print("Enter number of keys to randomly generate (Duplicates generated will be removed): ");
                int keyNum2 = sc.nextInt();
                List<Integer> testKeys2 = new ArrayList<>();
                List<Integer> testValues2 = new ArrayList<>();
                Random random2 = new Random();
                for (int i = 0; i < keyNum2; i++) {
                    testKeys2.add(random2.nextInt());
                    testValues2.add(random2.nextInt());
                }
                MultilevelStaticTable<Integer> test2 = new MultilevelStaticTable<>(32, testKeys2, testValues2);
                while (true) {
                    System.out.println("Select the operation you want to perform by pressing the respective number: ");
                    System.out.println("1:get collisions in first level");
                    System.out.println("2:get collisions in second level");
                    System.out.println("3:get number of cells");
                    System.out.println("4:exit");
                    int option = sc.nextInt();
                    switch (option) {
                        case 1 -> System.out.println(test2.getRehashesNum());
                        case 2 -> System.out.println(test2.getInnerRehashesNum());
                        case 3 -> System.out.println(test2.getNumOfCells());
                    }
                    if (option == 4) {
                        System.out.println("Exiting....");
                        break;
                    }
                }
            }
            case 3 -> System.out.println("Exiting....");
        }
    }
}


