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

    public Image getImage() {
        return image;
    }

    public Graph getGraph() {
        return graph;
    }

}
