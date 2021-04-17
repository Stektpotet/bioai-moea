package ga.data;


import moea.ProblemImSeg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @param <C> Chromosome
 */
public abstract class Population<ProblemT, C extends Chromosome<ProblemT>> extends ArrayList<C> {
    protected ProblemT problem;

    public Population(ProblemT problem, List<C> individuals) {
        this.addAll(individuals);
        this.problem = problem;
    }

    public C getOptimum() throws Exception {
        double minFitness = get(0).fitness(problem);
        C fittest = get(0);
        for (C c : this){
            double currentFitness = c.fitness(problem);
            if (currentFitness < minFitness) {
                minFitness = currentFitness;
                fittest = c;
            }
        }
        return fittest;
    }

    public ProblemT getProblem() {
        return problem;
    }
}
