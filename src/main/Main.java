package main;

import moea.ImSegFiles;
import moea.ga.Breeder;

import java.io.IOException;
public class Main {

    public static void main(String[] args) {
        try {
            var problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");
            var breeder = new Breeder(problem);
            var start = System.nanoTime();
            breeder.breed(500);
            System.out.println("it took " + ((System.nanoTime() - start)/1000000) + "ms");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
