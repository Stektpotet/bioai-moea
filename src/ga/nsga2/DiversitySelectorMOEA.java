package ga.nsga2;

import collections.ParetoImSeg;
import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.ga.PopulationImSeg;

import java.util.*;

public class DiversitySelectorMOEA implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    @Override
    public PopulationImSeg select(final PopulationImSeg generation, final List<ChromoImSeg> parents,
                                  final List<ChromoImSeg> offspring) {
        int size = generation.size();
        ProblemImSeg problem = generation.getProblem();

        List<ChromoImSeg> poolIndividuals = new ArrayList<>(generation.size() + offspring.size());
        poolIndividuals.addAll(generation);
        poolIndividuals.addAll(offspring);
        PopulationImSeg selectionPool = new PopulationImSeg(problem, poolIndividuals);
        ParetoImSeg sorted = NSGA2.FastNonDominatedSort(selectionPool);

        poolIndividuals.sort(sorted::compare);
        Set<ChromoImSeg> nextGenerationSet = new HashSet<>(generation.size());

        Iterator<ChromoImSeg> poolIterator = poolIndividuals.iterator();
        while (nextGenerationSet.size() < generation.size()) {
            if (!poolIterator.hasNext()) {
                break;
            }
            nextGenerationSet.add(poolIterator.next());
        }

        List<ChromoImSeg> nextGenerationList = new ArrayList<>(nextGenerationSet);
        if (nextGenerationSet.size() < generation.size()) {
            nextGenerationList.addAll(poolIndividuals.subList(0, generation.size() - nextGenerationList.size()));
        }

        return new PopulationImSeg(generation.getProblem(), nextGenerationList);
    }
}
