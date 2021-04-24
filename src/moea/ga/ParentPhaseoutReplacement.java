package moea.ga;

import ga.RandomUtil;
import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParentPhaseoutReplacement implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    private final ProblemImSeg problem;

    public ParentPhaseoutReplacement(ProblemImSeg problem) {
        this.problem = problem;
    }

    @Override
    public PopulationImSeg select(PopulationImSeg generation, List<ChromoImSeg> parents, List<ChromoImSeg> offspring) {
        List<ChromoImSeg> newGen = new ArrayList<>(generation);
        newGen.addAll(offspring);

        var elite = newGen.stream().min(UtilChromoImSeg.chromosomeFitnessComparator(problem)).get();
        newGen.add(elite);

        return new PopulationImSeg(problem, newGen.subList(offspring.size() + 1, newGen.size()));
    }
}