package moea.ga;

import ga.data.Population;
import ga.nsga2.NSGA2;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.List;

public class PopulationImSeg extends Population<ProblemImSeg, ChromoImSeg> {
    public PopulationImSeg(ProblemImSeg problem, List<ChromoImSeg> individuals) {
        super(problem, individuals);
    }

    @Override
    public List<ChromoImSeg> getOptima() throws Exception {
        return NSGA2.FastNonDominatedSort(this).getFront(0);
    }
}
