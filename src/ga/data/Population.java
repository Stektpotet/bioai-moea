package ga.data;


import java.util.List;

/**
 * @param <C> Chromosome
 */
public abstract class Population<ProblemT, C extends Chromosome<ProblemT>> {
    protected List<C> individuals;
    protected ProblemT problem;

    public Population(ProblemT problem, List<C> individuals) {
        this.individuals = individuals;
        this.problem = problem;
    }

    int getSize() {
        return individuals.size();
    }

    public List<C> getIndividuals() {
        return individuals;
    }

    public C getOptimum() throws Exception {
        double minFitness = individuals.get(0).fitness(problem);
        C fittest = individuals.get(0);
        for (C c : individuals){
            double currentFitness = c.fitness(problem);
            if (currentFitness < minFitness) {
                minFitness = currentFitness;
                fittest = c;
            }
        }
        return fittest;
    }
}
