package moea;

import collections.Graph;
import collections.Image;
import collections.Pixel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ProblemImSeg {
    private final Graph graph;
    private final Image image;

    public ProblemImSeg(Image image) {
        this.image = image;
        this.graph = new Graph(image);
    }

    public final int getPixelCount() { return image.getPixelCount(); }
    public final int getWith() {
        return image.getWidth();
    }

    public Graph.Edge[] getEdgesToNeighbors(Integer pixelIdx) {
        return null;
    }

    public final Pixel getPixel(int flatIndex) {
        return image.getPixel(flatIndex);
    }

    public Image getImage() {
        return image;
    }

    public Graph getGraph() {
        return graph;
    }

    public double sumMapOverEdges(final Function<Graph.Edge, Double> function) {
        double sum = 0;
        for (Integer pixelIdx : IntStream.range(0, this.getPixelCount())
                .collect(ArrayList<Integer>::new, List::add, List::addAll)) {
            double pixelSum = 0;
            Graph.Edge[] neigborEdges = this.getEdgesToNeighbors(pixelIdx);
            for (Graph.Edge neighborEdge : neigborEdges) {
                pixelSum += function.apply(neighborEdge);
            }
            sum += pixelSum;
        }
        return sum;
    }

}
