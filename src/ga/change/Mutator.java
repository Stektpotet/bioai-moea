package ga.change;

import ga.data.Chromosome;
import ga.data.Population;

import java.util.ArrayList;
import java.util.List;

public interface Mutator<ProblemT, P extends Population<ProblemT, C>, C extends Chromosome<ProblemT>> {
    public default List<C> mutateAll(P population, List<C> chromosomes) {
        List<C> mutated = new ArrayList<>(chromosomes.size());
        for (C c : chromosomes) {
            mutated.add(mutate(population, c));
        }
        return mutated;
    }
    C mutate(P population, final C chromosome);
}
