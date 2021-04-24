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

public class GeneticAlgorithmRunner<ProblemT, Pop extends Population<ProblemT, C>, C  extends Chromosome<ProblemT>> extends Service<GeneticAlgorithmSnapshot<C>> {

    private Initializer<ProblemT, Pop, C> initializer;
    private Recombinator<C> recombinator;
    private Mutator<ProblemT, C> mutator;
    private ParentSelector<ProblemT, C> parentSelector;
    private SurvivorSelector<ProblemT, Pop, C> survivorSelector;
    private final int populationSize;

    public GeneticAlgorithmRunner(Initializer<ProblemT, Pop, C> initializer,
                            Recombinator<C> recombinator,
                            Mutator<ProblemT, C> mutator,
                            ParentSelector<ProblemT, C> parentSelector,
                            SurvivorSelector<ProblemT, Pop, C> survivorSelector, int populationSize) {
        this.initializer = initializer;
        this.recombinator = recombinator;
        this.mutator = mutator;
        this.parentSelector = parentSelector;
        this.survivorSelector = survivorSelector;
        this.populationSize = populationSize;
    }

    @Override
    protected Task<GeneticAlgorithmSnapshot<C>> createTask() {
        return new Task<>() {
            @Override
            protected GeneticAlgorithmSnapshot<C> call() throws Exception {
                System.out.println("Starting GA...");
                var start = System.nanoTime();
                Pop pop = initializer.breed(populationSize);
                System.out.println("Breeding took: " + (System.nanoTime() - start)/1000000 + "ms");
                var generationCounter = IntStream.iterate(0, i -> i + 1).iterator();

                updateValue(new GeneticAlgorithmSnapshot<>(0, pop.getOptimum()));

                while (true) {
                    start = System.nanoTime();
                    Integer i = generationCounter.next();
                    System.out.println("Doing generation #" + i);
                    List<C> parents = parentSelector.select(pop);
                    List<C> offspring = mutator.mutateAll(recombinator.recombine(parents));
                    pop = survivorSelector.select(pop, parents, offspring);
                    C optimum = pop.getOptimum();
                    updateValue(new GeneticAlgorithmSnapshot<>(i, optimum));
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
