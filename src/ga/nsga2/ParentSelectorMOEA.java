package ga.nsga2;

import ga.selection.ParentSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.ga.PopulationImSeg;

import java.util.List;

public class ParentSelectorMOEA implements ParentSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    @Override
    public List<ChromoImSeg> select(PopulationImSeg population) {
        NSGA2.FastNonDominatedSort(population);

        return null;
    }
}
