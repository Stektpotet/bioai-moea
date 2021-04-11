package moea;

import collections.Graph;
import collections.Image;
import collections.Pixel;

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
}
