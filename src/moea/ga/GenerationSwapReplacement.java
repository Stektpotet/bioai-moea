package moea.ga;

import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.ArrayList;
import java.util.List;

public class GenerationSwapReplacement implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    private final ProblemImSeg problem;

    public GenerationSwapReplacement(ProblemImSeg problem) {
        this.problem = problem;
    }

    @Override
    public PopulationImSeg select(PopulationImSeg generation, List<ChromoImSeg> parents, List<ChromoImSeg> offspring) {
        return new PopulationImSeg(problem, offspring);
    }
}
