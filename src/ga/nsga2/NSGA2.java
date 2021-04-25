package ga.nsga2;

import collections.DefaultHashMap;
import collections.ParetoImSeg;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;
import moea.ga.PopulationImSeg;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NSGA2 {

    public static ParetoImSeg FastNonDominatedSort(PopulationImSeg solutions) {

        final ProblemImSeg problem = solutions.getProblem();
        // Replace the DefaultHashMap with HashMap and check component existence
        DefaultHashMap<Integer, List<Integer>> rankSortedFronts =  new DefaultHashMap<>(ArrayList::new);
        DefaultHashMap<Integer, Set<Integer>> dominatedBy = new DefaultHashMap<>(HashSet::new);

        int[] dominates = new int[solutions.size()];

        // 1. Compute domination relations between all solutions and pick out first front
        for (int p = 0; p < solutions.size(); p++) {
            for (int q = p + 1; q < solutions.size(); q++) {
                int pqDomination = UtilChromoImSeg.dominates(problem, solutions.get(p), solutions.get(q));
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

        return new ParetoImSeg(rankSorted, crowdingDistances(solutions.getProblem(), solutions));

    }

    private static Map<ChromoImSeg, Double> crowdingDistances(ProblemImSeg problem, PopulationImSeg pop) {
        //Map<ChromoImSeg, Double> distances = new HashMap<>(pop.size());
        DefaultHashMap<ChromoImSeg, Double> distances = new DefaultHashMap<>(() -> 0.0, pop.size());

        // connectivity
        addCrowdDistAlong(problem, pop, distances, ChromoImSeg.Fitness::getConnectivity);

        // edge value
        addCrowdDistAlong(problem, pop, distances, ChromoImSeg.Fitness::getEdge);

        // overall deviation
        addCrowdDistAlong(problem, pop, distances, ChromoImSeg.Fitness::getDeviation);
        
        return distances;
    }

    private static void addCrowdDistAlong(ProblemImSeg problem, PopulationImSeg pop, DefaultHashMap<ChromoImSeg,
            Double> distances, Function<ChromoImSeg.Fitness, Double> objective) {
        pop.sort(Comparator.comparingDouble(a -> objective.apply(a.calculateFitnessComponents(problem))));
        Iterator<ChromoImSeg> connectivityIterator = pop.iterator();

        ChromoImSeg forwardChromo = connectivityIterator.next();
        ChromoImSeg backwardChromo = connectivityIterator.next();

        distances.put(forwardChromo, Double.POSITIVE_INFINITY);

        double backwardsDistance;
        double forwardDistance;
        double distance;
        while (connectivityIterator.hasNext()) {
            distance = Math.abs(objective.apply(forwardChromo.calculateFitnessComponents(problem))
                    - objective.apply(backwardChromo.calculateFitnessComponents(problem)) * (1.0 / 6.0));
            
            forwardDistance = distances.get(forwardChromo);
            distances.put(forwardChromo, forwardDistance + distance);

            backwardsDistance = distances.get(backwardChromo);
            distances.put(backwardChromo, backwardsDistance + distance);
            
            forwardChromo = backwardChromo;
            backwardChromo = connectivityIterator.next();
        }

        distances.put(backwardChromo, Double.POSITIVE_INFINITY);
    }
}



