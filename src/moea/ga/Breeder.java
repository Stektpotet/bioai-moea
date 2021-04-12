package moea.ga;


import collections.Graph;
import ga.RandomUtil;
import ga.data.Initializer;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Breeder implements Initializer<ProblemImSeg, PopulationImSeg, ChromoImSeg> {

    private final ProblemImSeg problem;

    private static final class Node {
        private final int pixelIndex;
        private Node parent;
        private final Set<Node> children = new HashSet<>();

        Node(int pixelIndex) {
            this.pixelIndex = pixelIndex;
            this.parent = null;
        }

        Node(int pixelIndex, Node parent) {
            this.pixelIndex = pixelIndex;
            this.parent = parent;
        }

        public int getPixelIndex() {
            return pixelIndex;
        }

        public Node getParent() {
            return parent;
        }

        public Set<Node> getChildren() {
            return children;
        }

        void addChild(Node child) {
            children.add(child);
        }
        boolean removeChild(Node child) {
            child.parent = null;
            return children.remove(child);
        }
    }

    public Breeder(ProblemImSeg problem) {
        this.problem = problem;
    }

    @Override
    public PopulationImSeg breed(int popSize) {
        return new PopulationImSeg(
                problem,
                Stream.generate(this::makeIndividual).limit(popSize).collect(Collectors.toList())
        );
    }

    ChromoImSeg makeIndividual() {

        // 2. Make a MST out of the graph -> using prims algorithm, with a randomized starting point
        // TODO: Split the MST into several segments
        // 3.

        return new ChromoImSeg(problem, mst(problem));
    }

    private ChromoImSeg.EdgeOut[] mst(ProblemImSeg problem) {
        Graph graph = problem.getGraph();
        Node root = new Node(RandomUtil.random.nextInt(problem.getPixelCount()));
        Set<Integer> visited = new HashSet<>(problem.getPixelCount());
        visited.add(root.getPixelIndex());

        Map<Integer, Node> tree = new HashMap<>();
        tree.put(root.getPixelIndex(), root);

        // TODO: Uneducated guesstimate, initial capacity
        TreeSet<Graph.Edge> open = new TreeSet<>(graph.getCardinals(root.getPixelIndex()));

        while (visited.size() < problem.getPixelCount()) {
            Graph.Edge lowestCostEdge = open.pollFirst();

            int currentIndex = lowestCostEdge.getToIndex();
            if (visited.contains(currentIndex))
                continue;
            Node parent = tree.get(lowestCostEdge.getFromIndex());
            Node current = new Node(currentIndex, parent);
            parent.addChild(current);

            visited.add(current.getPixelIndex());

            open.addAll(graph.getCardinals(current.getPixelIndex()));
            open.remove(lowestCostEdge.flip()); // No point in moving back
            tree.put(currentIndex, current);
        }

       return IntStream.range(0, problem.getPixelCount()).mapToObj(
                i -> calcDirection(tree.get(i))
        ).toArray(ChromoImSeg.EdgeOut[]::new);
    }

    static ChromoImSeg.EdgeOut calcDirection(Node n) {
        if (n.parent == null) {
            return ChromoImSeg.EdgeOut.NONE;
        }
        var in = n.pixelIndex;
        var ip = n.parent.pixelIndex;
        if (ip < in) { // the direction is either left or up
            if (ip + 1 == in) { // left
                return ChromoImSeg.EdgeOut.LEFT;
            }
            return ChromoImSeg.EdgeOut.UP;
        }
        else {
            if (ip - 1 == in) {
                return ChromoImSeg.EdgeOut.RIGHT;
            }
            return ChromoImSeg.EdgeOut.DOWN;
        }
    }
//    void traverseDepthFirst(Node n) {
//        for (Node c : n.children) {
//            traverseDepthFirst(c);
//        }
//    }
}
