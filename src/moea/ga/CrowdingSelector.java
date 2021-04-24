package moea.ga;


import ga.RandomUtil;
import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CrowdingSelector implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    private final int crowdingFactor;
    private final ProblemImSeg problem;

    public CrowdingSelector(final ProblemImSeg problem, final int crowdingFactor) {
        this.problem = problem;
        this.crowdingFactor = crowdingFactor;
    }
    @Override
    public PopulationImSeg select(PopulationImSeg generation, List<ChromoImSeg> parents, List<ChromoImSeg> offspring) {

        for (ChromoImSeg child : offspring) {
            Comparator<ChromoImSeg> differenceComparator = Comparator.comparingInt(a -> UtilChromoImSeg.hammingDistance(a, child));

            List<ChromoImSeg> comparisonPool = RandomUtil.randomChoice(generation, crowdingFactor, false);
            ChromoImSeg mostSimilarParent = Collections.min(comparisonPool, differenceComparator);
            
            if (mostSimilarParent.fitness(problem) > child.fitness(problem)) {
                generation.remove(mostSimilarParent);
                generation.add(child);
            }
        }

        return generation;
    }
}
