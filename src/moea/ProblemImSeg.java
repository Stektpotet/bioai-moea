package moea;

import collections.Graph;
import collections.Image;
import collections.Pixel;

import java.util.function.Function;

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

        for (int i = 0; i < this.getPixelCount(); i++) {
            for (var neighbourEdge : graph.getAdjacent(i)) {
                if (!neighbourEdge.valid())
                    continue;
                sum += function.apply(neighbourEdge);
            }
        }
        return sum;
    }

}
