package collections;

import moea.ChromoImSeg;

import java.util.*;

public class ParetoImSeg {
    List<List<ChromoImSeg>> fronts;
    Map<ChromoImSeg, Integer> rankMap;

    public ParetoImSeg(List<List<ChromoImSeg>> fronts) {
        this.fronts = List.copyOf(fronts);
        this.rankMap = new HashMap<>();
        for (int i = 1; i <= this.fronts.size(); i++) {
            for (ChromoImSeg chromosome : this.fronts.get(i)) {
                rankMap.put(chromosome, i);
            }
        }
        this.rankMap = Collections.unmodifiableMap(rankMap);
    }

    public int getFrontRank(ChromoImSeg chromosome) {
        return rankMap.get(chromosome);
    }

    public List<ChromoImSeg> getFront(int i) {
        return fronts.get(i);
    }
}
