package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


public class Graph {


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

    private ArrayList<Edge>[] adjacencyList;
//    private Edge[][] adjacencyList;

    public List<Edge> getAdjacent(int flatIndex) {
        return Collections.unmodifiableList(adjacencyList[flatIndex]);
    }
    @SuppressWarnings("unchecked")
    public Graph(Image img) {
        adjacencyList = Stream.generate(() -> new ArrayList<Edge>(4)).limit(img.getPixelCount()).toArray(ArrayList[]::new);

        for (int i = 0; i < adjacencyList.length; i++) {
            Pixel p = img.getPixel(i);

            int ix = i % img.getWidth();
            int iy = i / img.getWidth();
            if (ix < img.getWidth() - 1)
                adjacencyList[i].add(new Edge(i,i + 1, p.distance(img.getPixel(ix + 1, iy))));
            if (ix > 0)
                adjacencyList[i].add(new Edge(i,i - 1, p.distance(img.getPixel(ix - 1, iy))));
            if (iy < img.getHeight() - 1)
                adjacencyList[i].add(new Edge(i,i + img.getWidth(), p.distance(img.getPixel(ix, iy + 1))));
            if (iy > 0)
                adjacencyList[i].add(new Edge(i,i - img.getWidth(), p.distance(img.getPixel(ix, iy - 1))));
        }
    }
}
