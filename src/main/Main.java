package main;

import moea.ImSegFiles;
import moea.ga.Breeder;

public class Main {

    public static void main(String[] args) {
        var problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");
        var breeder = new Breeder(problem, 4, 50);
        var start = System.nanoTime();
        breeder.breed(5);
        System.out.println("it took " + ((System.nanoTime() - start)/1000000) + "ms");
    }
}
