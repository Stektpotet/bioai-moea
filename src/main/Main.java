package main;

import collections.DefaultHashMap;
import ga.data.Chromosome;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        DefaultHashMap<Integer, Integer> test = new DefaultHashMap<>(() -> 0);
        DefaultHashMap<Integer, Set<Integer>> test2 = new DefaultHashMap<>(HashSet::new);

        Comparator<Integer> comparator = Integer::compareTo;

        System.out.println(comparator.compare(1, 0));
        System.out.println(comparator.compare(1, 1));
        System.out.println(comparator.compare(0, 1));
    }
}
