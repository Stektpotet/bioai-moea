package ga.change;

import ga.data.Chromosome;
import ga.data.Population;

import java.util.ArrayList;
import java.util.List;

public interface Mutator<ProblemT, C extends Chromosome<ProblemT>> {
    public default List<C> mutateAll(List<C> chromosomes) {
        List<C> mutated = new ArrayList<>(chromosomes.size());
        for (C c : chromosomes) {
            mutated.add(mutate(c));
        }
        return mutated;
    }
    C mutate(final C chromosome);
}
