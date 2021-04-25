package ga.nsga2;

import ga.RandomUtil;
import ga.selection.ParentSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;
import moea.ga.PopulationImSeg;

import java.util.ArrayList;
import java.util.List;

public class ParentSelectorMOEA implements ParentSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {
    private final int numParents;
    private final int tournamentSize;

    @Override
    public List<ChromoImSeg> select(PopulationImSeg population) throws Exception {
        if (tournamentSize <= 1) {
            throw new Exception("No tournaments without competitors!");
        }
        if (population.size() < tournamentSize) {
            throw new Exception("Not enough individuals for the tournament found in the population!");
        }

        var sorted = NSGA2.FastNonDominatedSort(population);

        List<ChromoImSeg> parents = new ArrayList<>(numParents);
        for (int i = 0; i < numParents; i++) {
            List<ChromoImSeg> pool =  RandomUtil.randomChoice(population, tournamentSize, false);
            // TODO: Check sorting order
            parents.add(pool.stream().max(sorted::compare).get());
        }
        return parents;
    }

    public ParentSelectorMOEA(int numParents, int tournamentSize) throws Exception {
        if (numParents % 2 != 0) {
            throw new Exception("Number of parents has to be even!");
        }
        if (tournamentSize < 1) {
            throw new Exception("Tournament size has to be at least 1!");
        }
        this.numParents = numParents;
        this.tournamentSize = tournamentSize;
    }

}
