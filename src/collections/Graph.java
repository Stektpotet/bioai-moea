package collections;

import java.util.ArrayList;
import java.util.stream.Stream;



public class Graph {
    Image nodes;
    ArrayList<Edge>[] adjacencies;

    @SuppressWarnings("unchecked")
    public Graph(Image img) {
        nodes = img;
        adjacencies = Stream.generate(() -> new ArrayList<Edge>(4)).limit(img.getPixelCount()).toArray(ArrayList[]::new);

        for (int i = 0; i < nodes.getPixelCount(); i++) {
            Pixel p = nodes.getPixel(i);

            int ix = i % img.getWidth();
            int iy = i / img.getWidth();
            if (ix < img.getWidth() - 1)
                adjacencies[i].add(new Edge(i + 1, p.distance(nodes.getPixel(i + 1))));
            if (ix > 0)
                adjacencies[i].add(new Edge(i - 1, p.distance(nodes.getPixel(i - 1))));
            if (iy < img.getHeight() - 1)
                adjacencies[i].add(new Edge(i + img.getWidth(), p.distance(nodes.getPixel(i + img.getWidth()))));
            if (iy > 0)
                adjacencies[i].add(new Edge(i - img.getWidth(), p.distance(nodes.getPixel(i - img.getWidth()))));
        }
    }
}



class Edge {
    final int toIndex;
    final double cost;

    Edge(int toIndex, double cost) {
        this.toIndex = toIndex;
        this.cost = cost;
    }
}