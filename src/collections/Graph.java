package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


public class Graph {

    private static final byte
            UPPER_LEFT = 0,
            UPPER = 1,
            UPPER_RIGHT = 2,
            LEFT = 3,
            RIGHT = 4,
            LOWER_LEFT = 5,
            LOWER = 6,
            LOWER_RIGHT = 7;

    public static class Edge {
        private final int fromIndex; // It's not that nice that we have to do this :/
        private final int toIndex;
        private final double cost;

        Edge(int fromIndex, int toIndex, double cost) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.cost = cost;
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
    }

    // TODO: this should really hold one adjacency-list of based on moore-neighbourhoods
    //       but also a way of accessing only the cardinal neighbours
    //       DISCUSS:
    //          Should the adjacency list be Edge[][] instead?
    //          - then we can index the moore neighbourhood like described in the task
    //          - what do we then do with the non-existing neighbours of corner and edging pixels?
    //              - We set it to -1

//    private ArrayList<Edge>[] adjacencyList;
    private Edge[][] adjacencyList;

    public Edge[] getAdjacent(int flatIndex) {
        return adjacencyList[flatIndex];
    }

    /*
    *   Neighbourhood layout
    *   7   3   5
    *   2   P   1
    *   8   4   6
    */

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
                                (ix == img.getWidth() - 1 && (j == UPPER_RIGHT || j == RIGHT || j == LOWER_RIGHT))
                )
                    adjacencyList[i][j] = invalidEdge;
                else
                    adjacencyList[i][j] = new Edge(i, neighbour, p.distance(img.getPixel(neighbour)));
            }
        }
    }
}
