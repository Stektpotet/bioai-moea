package collections;

import ga.data.Chromosome;

import java.util.*;

public class Pareto<TProblem, TChromosome extends Chromosome<TProblem>>  {
    List<List<TChromosome>> fronts;
    Map<TChromosome, Integer> rankMap;

    public Pareto(List<List<TChromosome>> fronts) {
        this.fronts = List.copyOf(fronts);
        this.rankMap = new HashMap<>();
        for (int i = 1; i <= this.fronts.size(); i++) {
            for (TChromosome chromosome : this.fronts.get(i)) {
                rankMap.put(chromosome, i);
            }
        }
        this.rankMap = Collections.unmodifiableMap(rankMap);
    }

    public int getFrontRank(TChromosome chromosome) {
        return rankMap.get(chromosome);
    }

    public List<TChromosome> getFront(int i) {
        return fronts.get(i);
    }
}
