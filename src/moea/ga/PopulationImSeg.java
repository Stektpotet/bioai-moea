package moea.ga;

import ga.data.Population;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.List;

public class PopulationImSeg extends Population<ProblemImSeg, ChromoImSeg> {
    public PopulationImSeg(ProblemImSeg problem, List<ChromoImSeg> individuals) {
        super(problem, individuals);
    }
}
