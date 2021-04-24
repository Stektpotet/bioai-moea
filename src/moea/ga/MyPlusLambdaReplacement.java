package moea.ga;

import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.ArrayList;
import java.util.List;

public class MyPlusLambdaReplacement implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    private final ProblemImSeg problem;

    public MyPlusLambdaReplacement(ProblemImSeg problem) {
        this.problem = problem;
    }

    @Override
    public PopulationImSeg select(PopulationImSeg generation, List<ChromoImSeg> parents, List<ChromoImSeg> offspring) {
        ArrayList<ChromoImSeg> myPlusLa = new ArrayList<>(generation);
        myPlusLa.addAll(offspring);
        myPlusLa.sort(UtilChromoImSeg.chromosomeFitnessComparator(problem));
        return new PopulationImSeg(problem, myPlusLa.subList(0, generation.size()));
    }
}
