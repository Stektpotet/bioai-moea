package main;

import collections.DefaultHashMap;
import collections.Graph;
import ga.data.Chromosome;
import moea.ImSegFiles;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try {
            var problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");

            Runtime runtime = Runtime.getRuntime();

            long startTime = System.nanoTime();

            long before = runtime.totalMemory() - runtime.freeMemory();
            Graph g = new Graph(problem.getImage());
            long after = runtime.totalMemory() - runtime.freeMemory();

            long endTime = System.nanoTime();
            System.out.println(String.format("%dms",(endTime - startTime)/1000000));

            System.out.println("test memory: " + (after - before));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
