package ga.nsga2;

import ga.data.Population;
import ga.selection.ParentSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.List;

public class ParentSelectorMOEA implements ParentSelector<ProblemImSeg, ChromoImSeg> {

    @Override
    public List<ChromoImSeg> select(Population<ProblemImSeg, ChromoImSeg> population) throws Exception {
        return null;
    }
}
