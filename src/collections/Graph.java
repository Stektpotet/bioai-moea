package collections;

import java.util.*;
import java.util.stream.Stream;


public class Graph {

    private static final byte
            UPPER_LEFT = 0,
            UP = 1,
            UPPER_RIGHT = 2,
            LEFT = 3,
            RIGHT = 4,
            LOWER_LEFT = 5,
            DOWN = 6,
            LOWER_RIGHT = 7;

    public static class Edge implements Comparable<Edge> {
        private final int fromIndex; // It's not that nice that we have to do this :/
        private final int toIndex;
        private final double cost;

        Edge(int fromIndex, int toIndex, double cost) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.cost = cost;
        }

        public Edge flip() {
            return new Edge(toIndex, fromIndex, cost);
        }

        public boolean valid() {
            return fromIndex != -1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return ((this.toIndex == edge.toIndex && this.fromIndex == edge.fromIndex) ||
                    (this.fromIndex == edge.toIndex && this.toIndex == edge.fromIndex)) &&
                            Double.compare(edge.cost, cost) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fromIndex, toIndex, cost);
        }

        public int getFromIndex() {
            return fromIndex;
        }

        public int getToIndex() {
            return toIndex;
        }

        public double getCost() {
            return cost;
        }

        @Override
        public int compareTo(Edge o) {
            int comparison = Double.compare(this.cost, o.cost);
            if (comparison == 0) {
                if ((this.toIndex == o.toIndex && this.fromIndex == o.fromIndex) ||
                        (this.fromIndex == o.toIndex && this.toIndex == o.fromIndex))
                    return 0;
                return 1;
            }
            return comparison;
        }
    }

    //       this should really hold one adjacency-list of based on moore-neighbourhoods
    //       but also a way of accessing only the cardinal neighbours
    //       DISCUSS:
    //          Should the adjacency list be Edge[][] instead?
    //          - then we can index the moore neighbourhood like described in the task
    //          - what do we then do with the non-existing neighbours of corner and edging pixels?
    //              - We set it to -1

    private Edge[][] adjacencyList;

    public List<Edge> getAdjacent(int flatIndex) {
        return List.of(adjacencyList[flatIndex]); // returns an unmodifiable list
    }
    public Stream<Edge> streamValidNeighbours(int flatIndex) {
        return Arrays.stream(adjacencyList[flatIndex]).filter(Edge::valid);
    }

    public List<Edge> getCardinals(int flatIndex) {
        Edge[] moores = adjacencyList[flatIndex];
        return List.of(moores[UP], moores[LEFT], moores[RIGHT], moores[DOWN]);
    }
    public Graph(Image img) {
        adjacencyList = new Edge[img.getPixelCount()][8];
        int[] mooresRelations = new int[]{
                -img.getWidth() - 1, -img.getWidth(), -img.getWidth() + 1,
                -1,                                   +1,
                 img.getWidth() - 1,  img.getWidth(),  img.getWidth() + 1
        };
        Edge invalidEdge = new Edge(-1, -1, Double.POSITIVE_INFINITY);
        for (int i = 0; i < adjacencyList.length; i++) {
            Pixel p = img.getPixel(i);
            int ix = i % img.getWidth();

            for (int j = 0; j < mooresRelations.length; j++) {
                int neighbour = i + mooresRelations[j];
                if (
                    // Deal with neighbours outside along the vertical axis of the image
                        neighbour < 0 || neighbour >= img.getPixelCount() ||
                    // Deal with neighbours outside along the horizontal axis of the image
                        (ix == 0 && (j == UPPER_LEFT || j == LEFT || j == LOWER_LEFT)) ||
                                (ix == (img.getWidth() - 1) && (j == UPPER_RIGHT || j == RIGHT || j == LOWER_RIGHT))
                )
                    adjacencyList[i][j] = invalidEdge;
                else
                    adjacencyList[i][j] = new Edge(i, neighbour, p.distance(img.getPixel(neighbour)));
            }
        }
    }
}
