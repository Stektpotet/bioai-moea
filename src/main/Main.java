package main;

import ga.selection.SurvivorSelector;
import moea.ImSegFiles;
import moea.ga.Breeder;

import java.io.IOException;
public class Main {

    public static void main(String[] args) {
        var problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");
        var breeder = new Breeder(problem);
        var start = System.nanoTime();
        breeder.breed(5);
        System.out.println("it took " + ((System.nanoTime() - start)/1000000) + "ms");
    }
}
