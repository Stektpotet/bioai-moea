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

//
//    public double segmentationSummation(final List<Set<Integer>> segments, final Function<Graph.Edge, Double> fun) {
//        double sum = 0;
//        for (var segment : segments) {
//            for (var pixelIndex : segment) {
//                sum += graph.streamValidNeighbours(pixelIndex).mapToDouble(e -> 0.0).sum();
//            }
//        }
//        // TODO: make it so we iterate through the segments one by one instead,
//        //       then we don't need to find whether a segment holds both pixels,
//        //       we just check if the neighbouring pixel is in the same one!
//        return sum;
//    }
}

// fun(inSameSegment, pixelDistance)