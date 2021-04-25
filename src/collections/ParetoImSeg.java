package collections;

import moea.ChromoImSeg;

import java.util.*;

public class ParetoImSeg {
    final List<List<ChromoImSeg>> fronts;
    final Map<ChromoImSeg, Integer> rankMap;
    final Map<ChromoImSeg, Double> crowdingDistances;

    public ParetoImSeg(List<List<ChromoImSeg>> fronts, Map<ChromoImSeg, Double> crowdingDistances) {
        this.fronts = List.copyOf(fronts);
        Map<ChromoImSeg, Integer> rankMap = new HashMap<>();
        for (int i = 1; i <= this.fronts.size(); i++) {
            for (ChromoImSeg chromosome : this.fronts.get(i)) {
                rankMap.put(chromosome, i);
            }
        }
        this.rankMap = Collections.unmodifiableMap(rankMap);
        this.crowdingDistances = Collections.unmodifiableMap(crowdingDistances);
    }

    public int getFrontRank(ChromoImSeg chromosome) {
        return rankMap.get(chromosome);
    }

    public List<ChromoImSeg> getFront(int i) {
        return fronts.get(i);
    }

    public double getCrowdingDistance(ChromoImSeg chromosome) {
        return crowdingDistances.get(chromosome);
    }
}
