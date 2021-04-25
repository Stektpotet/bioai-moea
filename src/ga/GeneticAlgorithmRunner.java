package ga;

import ga.change.Mutator;
import ga.change.Recombinator;
import ga.data.Chromosome;
import ga.data.Initializer;
import ga.data.Population;
import ga.selection.ParentSelector;
import ga.selection.SurvivorSelector;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;
import java.util.stream.IntStream;

public class GeneticAlgorithmRunner<TProblem, TPop extends Population<TProblem, TChromo>, TChromo extends Chromosome<TProblem>> extends Service<GeneticAlgorithmSnapshot<TChromo>> {

    private final Initializer<TProblem, TPop, TChromo> initializer;
    private final Recombinator<TChromo> recombinator;
    private final Mutator<TProblem, TChromo> mutator;
    private final ParentSelector<TProblem, TPop, TChromo> parentSelector;
    private final SurvivorSelector<TProblem, TPop, TChromo> survivorSelector;
    private final int populationSize;

    public GeneticAlgorithmRunner(Initializer<TProblem, TPop, TChromo> initializer,
                                  Recombinator<TChromo> recombinator,
                                  Mutator<TProblem, TChromo> mutator,
                                  ParentSelector<TProblem, TPop, TChromo> parentSelector,
                                  SurvivorSelector<TProblem, TPop, TChromo> survivorSelector, int populationSize) {
        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
        this.populationSize = populationSize;
    }

    @Override
    protected Task<GeneticAlgorithmSnapshot<TChromo>> createTask() {
        return new Task<>() {
            @Override
            protected GeneticAlgorithmSnapshot<TChromo> call() throws Exception {
                System.out.println("Starting GA...");
                var start = System.nanoTime();
                TPop pop = initializer.breed(populationSize);
                System.out.println("Breeding took: " + (System.nanoTime() - start)/1000000 + "ms");
                var generationCounter = IntStream.iterate(0, i -> i + 1).iterator();

                updateValue(new GeneticAlgorithmSnapshot<>(0, pop.getOptima()));

                while (true) {
                    start = System.nanoTime();
                    Integer i = generationCounter.next();
                    System.out.println("Doing generation #" + i);
                    List<TChromo> parents = parentSelector.select(pop);
                    List<TChromo> offspring = mutator.mutateAll(recombinator.recombine(parents));
                    pop = survivorSelector.select(pop, parents, offspring);
                    List<TChromo> optima = pop.getOptima();
                    updateValue(new GeneticAlgorithmSnapshot<>(i, optima));
                    System.out.println("Generation #" + i + " took: " + (System.nanoTime() - start)/1000000 + "ms");
                }
            }
        };
    }

    @Override
    protected void succeeded() {
        System.out.println("Task Completed Successfully!");
    }
}
