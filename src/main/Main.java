package main;

import moea.ImSegFiles;
import moea.ga.Breeder;
import moea.ga.MutatorImSeg;
import moea.ga.MyPlusLambdaReplacement;
import moea.ga.UniformCrossoverer;

public class Main {

    public static void main(String[] args) throws Exception {
//        var problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");
//        var breeder = new Breeder(problem, 4, 50);
//        var start = System.nanoTime();
//        var pop = breeder.breed(2);
//        var crossover = new UniformCrossoverer(0.5f);
//        var survivorSelector = new MyPlusLambdaReplacement(problem);
//        var mutator = new MutatorImSeg(0.17f);
//        var children = crossover.recombine(pop);
//        var mutatedChildren = mutator.mutateAll(children);

        System.out.println("it took " + 241 % 241 + ", " + 240 % 241 + ", " + Math.floorMod(-1, 241));
    }
}
