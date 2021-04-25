package ga;

import ga.data.Chromosome;

import java.util.List;


/**
 * A snapshot of the current state of the genetic algorithm, representing the best
 * @param <TChromo>
 */
public class GeneticAlgorithmSnapshot<TChromo extends Chromosome> {
    public final int currentGeneration;
    public final List<TChromo> optima;
    GeneticAlgorithmSnapshot(int currentGeneration, List<TChromo> optima) {
        this.currentGeneration = currentGeneration;
        this.optima = optima;
    }
}
