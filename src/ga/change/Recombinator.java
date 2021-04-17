package ga.change;

import ga.data.Chromosome;

import java.util.List;

public interface Recombinator<C extends Chromosome> {
    public List<C> recombine(final List<C> parents) throws Exception;
    List<C> crossover(final C mum, final C dad);
}
