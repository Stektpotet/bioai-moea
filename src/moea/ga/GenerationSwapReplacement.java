package moea.ga;

import ga.RandomUtil;
import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerationSwapReplacement implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {


    private final ProblemImSeg problem;

    public GenerationSwapReplacement(ProblemImSeg problem) {
        this.problem = problem;
    }

    @Override
    public PopulationImSeg select(PopulationImSeg generation, List<ChromoImSeg> parents, List<ChromoImSeg> offspring) {
        // find best one percent of generation - elite
        ChromoImSeg elite = Collections.min(generation, UtilChromoImSeg.chromosomeFitnessComparator(problem));

        // take parents out of generation
        for (var p : parents) {
            generation.remove(p);
        }

        // put offspring into generation
        generation.addAll(offspring);

        // randomly pop one percent of generation - r
        ChromoImSeg randomlyChosen = RandomUtil.randomChoiceRemove(generation);

        // put best 50 % of elite and r back into the population
        generation.add((elite.fitness(problem) < randomlyChosen.fitness(problem)) ? elite : randomlyChosen);

        return generation;
    }
}