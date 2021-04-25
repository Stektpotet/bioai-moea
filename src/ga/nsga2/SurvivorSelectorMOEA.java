package ga.nsga2;

import collections.ParetoImSeg;
import ga.selection.SurvivorSelector;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;
import moea.ga.PopulationImSeg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SurvivorSelectorMOEA implements SurvivorSelector<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

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


        List<ChromoImSeg> nextGeneration = new ArrayList<>(size);
        int frontCount = 1;
        var front = sorted.getFront(frontCount);
        while (nextGeneration.size() + front.size() < size) {
            nextGeneration.addAll(front);
        }

        front.sort(Comparator.comparingDouble(sorted::getCrowdingDistance));
        //TODO: check if sorted the right way around!
        while (nextGeneration.size() < size) {
            nextGeneration.add(front.remove(0));
        }

        return new PopulationImSeg(problem, nextGeneration);


    }
}
