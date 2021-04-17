package moea.ga;

import ga.RandomUtil;
import ga.data.Population;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.ArrayList;
import java.util.List;

public class TournamentSelection implements ga.selection.ParentSelector<ProblemImSeg, ChromoImSeg> {

    private final int numParents;
    private final int tournamentSize;

    @Override
    public List<ChromoImSeg> select(Population<ProblemImSeg, ChromoImSeg> population) throws Exception {
        if (population.size() < tournamentSize) {
            throw new Exception("Not enough individuals for the tournament found in the population!");
        }

        List<ChromoImSeg> parents = new ArrayList<>(numParents);
        for (int i = 0; i < numParents; i++) {
            List<ChromoImSeg> pool =  RandomUtil.randomChoice(population, tournamentSize, false);
            parents.add(
                    pool.stream().max(UtilChromoImSeg.chromosomeFitnessComparator(population.getProblem())).get());
        }
        return parents;
    }

    public TournamentSelection(int numParents, int tournamentSize) throws Exception {
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
