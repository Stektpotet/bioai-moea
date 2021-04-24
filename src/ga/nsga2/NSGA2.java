package ga.nsga2;

import collections.DefaultHashMap;
import ga.data.Chromosome;
import moea.ChromoImSeg;
import moea.ga.PopulationImSeg;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class NSGA2 {

    public static <Pop> List<List<ChromoImSeg>> FastNonDominatedSort(PopulationImSeg solutions, Comparator<ChromoImSeg> domination) {

        // Replace the DefaultHashMap with HashMap and check component existence
        DefaultHashMap<Integer, List<Integer>> rankSortedFronts =  new DefaultHashMap<>(ArrayList::new);
        DefaultHashMap<Integer, Set<Integer>> dominatedBy = new DefaultHashMap<>(HashSet::new);

        int[] dominates = new int[solutions.size()];

        // 1. Compute domination relations between all solutions and pick out first front
        for (int p = 0; p < solutions.size(); p++) {
            for (int q = p + 1; q < solutions.size(); q++) {
                int pqDomination = domination.compare(solutions.get(p), solutions.get(q));
                if (pqDomination > 0) {
                    dominatedBy.get(p).add(q);  // P dominates Q
                    dominates[q]++;             // increase number of solutions dominating Q
                }
                else if (pqDomination < 0) {
                    dominatedBy.get(q).add(p);  // Q dominates P
                    dominates[p]++;             // increase number of solutions dominating P
                }
            }
            // Pick out first front elements
            if (dominates[p] == 0) { // No other solutions dominated p -> p belongs to the first front
                rankSortedFronts.get(1).add(p);
            }
        }
        // 2. Iterate through higher ranks by decrementing the 'dominates'-counter,
        // and looking up 'dominatedBy' on current front elements
        int frontRank = 1;
        List<Integer> front = rankSortedFronts.get(frontRank);
        while (!front.isEmpty()) {
            for (Integer p : front) {
                for (Integer q : dominatedBy.get(p)) {
                    if (--dominates[q] == 0) {
                        rankSortedFronts.get(frontRank + 1).add(q);
                    }
                }
            }
            front = rankSortedFronts.get(++frontRank);
        }

        // 3. Build a feasible return value with solutions sorted by rank of non-domination
        List<List<ChromoImSeg>> rankSorted = new ArrayList<>(rankSortedFronts.size());
        for (int i = 1; i <= rankSortedFronts.size(); i++) {
            rankSorted.add(rankSortedFronts.get(i).stream().map(solutions::get).collect(Collectors.toList()));
        }
        return Collections.unmodifiableList(rankSorted);
    }
}
