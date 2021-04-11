package moea.ga;


import collections.Graph;
import collections.Image;
import ga.RandomUtil;
import ga.data.Initializer;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Breeder implements Initializer<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    @Override
    public PopulationImSeg breed(int popSize) {
        return null;
    }

    ChromoImSeg makeIndividual() {
        // 1. Make a graph from the image -> should this be stored aside so we don't need to recalculate weights?

        // 2. Make a MST out of the graph -> using prims algorithm, with a randomized starting point

        // 3.

        return null;
    }

    private ChromoImSeg.EdgeOut[] mst(ProblemImSeg problem) {
        Graph graph = problem.getGraph();  // TODO: we could also consider moving the functionality from the Graph
        Image image = problem.getImage();  //       and Image class out into ProblemImSeg - more readability?
        int startingNode = RandomUtil.random.nextInt(problem.getPixelCount());
        Set<Graph.Edge> open = new HashSet<>();
        int node = startingNode;
        while (!open.isEmpty()) {
            var lowestCostEdge = open.stream().min(Comparator.comparingDouble(Graph.Edge::getCost)).get();
        }
        return null;
    }

}
