package ga.data;


import java.util.ArrayList;
import java.util.List;

/**
 * @param <TChromo> Chromosome
 */
public abstract class Population<TProblem, TChromo extends Chromosome<TProblem>> extends ArrayList<TChromo> {
    protected TProblem problem;

    public Population(TProblem problem, List<TChromo> individuals) {
        this.addAll(individuals);
        this.problem = problem;
    }

    public List<TChromo> getOptima() throws Exception {
        double minFitness = get(0).fitness(problem);
        TChromo fittest = get(0);
        for (TChromo c : this){
            double currentFitness = c.fitness(problem);
            if (currentFitness < minFitness) {
                minFitness = currentFitness;
                fittest = c;
            }
        }
        return List.of(fittest);
    }

    public TProblem getProblem() {
        return problem;
    }
}
