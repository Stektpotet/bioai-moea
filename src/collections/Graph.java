package collections;

import java.util.*;
import java.util.stream.Stream;


public class Graph {

    private static final byte
            UPPER_LEFT = 0,
            UPPER_RIGHT = 1,
            LOWER_LEFT = 2,
            LOWER_RIGHT = 3,
            UP = 0,
            LEFT = 1,
            RIGHT = 2,
            DOWN = 3;

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

    private List<List<Edge>> adjacencyList;
    private List<List<Edge>> cardinalAdjacencyList;

    public List<Edge> getAdjacent(int flatIndex) {
        return Collections.unmodifiableList(adjacencyList.get(flatIndex)); // returns an unmodifiable list
    }

    public List<Edge> getCardinals(int flatIndex) {
        return Collections.unmodifiableList(cardinalAdjacencyList.get(flatIndex));
    }
    public Graph(Image img) {
        adjacencyList = new ArrayList<>(img.getPixelCount());
        cardinalAdjacencyList = new ArrayList<>(img.getPixelCount());

        int[] corners = new int[]{
                -img.getWidth() - 1, -img.getWidth() + 1,
                 img.getWidth() - 1,  img.getWidth() + 1
        };
        int[] cardinals = new int[]{
                -img.getWidth(), -1, +1, img.getWidth()
        };

        for (int i = 0; i < img.getPixelCount(); i++) {
            Pixel p = img.getPixel(i);
            int ix = i % img.getWidth();
            List<Edge> neighbours = new ArrayList<>(8);
            List<Edge> cardinalNeighbours = new ArrayList<>(4);
            { // CARDINALS
                int neighbour = i + cardinals[UP];
                if (neighbour >= 0) {
                    Edge e = new Edge(i, neighbour, p.distance(img.getPixel(neighbour)));
                    neighbours.add(e);
                    cardinalNeighbours.add(e);
                }
                neighbour = i + cardinals[LEFT];
                if (Math.floorMod(neighbour, img.getWidth()) != (img.getWidth() - 1)) {
                    Edge e = new Edge(i, neighbour, p.distance(img.getPixel(neighbour)));
                    neighbours.add(e);
                    cardinalNeighbours.add(e);
                }
                neighbour = i + cardinals[RIGHT];
                if (neighbour % img.getWidth() != 0) {
                    Edge e = new Edge(i, neighbour, p.distance(img.getPixel(neighbour)));
                    neighbours.add(e);
                    cardinalNeighbours.add(e);
                }
                neighbour = i + cardinals[DOWN];
                if (neighbour < img.getPixelCount()) {
                    Edge e = new Edge(i, neighbour, p.distance(img.getPixel(neighbour)));
                    neighbours.add(e);
                    cardinalNeighbours.add(e);
                }
            }
            { // CORNERS
                int neighbour = i + corners[UPPER_LEFT];
                if (neighbour >= 0) {
                    neighbours.add(new Edge(i, neighbour, p.distance(img.getPixel(neighbour))));
                }
                neighbour = i + corners[UPPER_RIGHT];
                if (neighbour >= 0) {
                    neighbours.add(new Edge(i, neighbour, p.distance(img.getPixel(neighbour))));
                }
                neighbour = i + corners[LOWER_LEFT];
                if (neighbour < img.getPixelCount()) {
                    neighbours.add(new Edge(i, neighbour, p.distance(img.getPixel(neighbour))));
                }
                neighbour = i + corners[LOWER_RIGHT];
                if (neighbour < img.getPixelCount()) {
                    neighbours.add(new Edge(i, neighbour, p.distance(img.getPixel(neighbour))));
                }
            }
            adjacencyList.add(neighbours);
            cardinalAdjacencyList.add(cardinalNeighbours);
        }
    }
}
