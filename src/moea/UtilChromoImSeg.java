package moea;

import collections.Graph;
import collections.Pixel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UtilChromoImSeg {

    public static double overallDeviation(final ProblemImSeg image, final List<Set<Integer>> segments) {
        double overallDeviation = 0;
        for (Set<Integer> segment : segments) {
            Pixel centroid = centroid(image, segment);
            double segmentDeviation = segment.stream().map(image::getPixel).mapToDouble(centroid::distance).sum();
            overallDeviation += segmentDeviation;
        }
        return overallDeviation;
    }

    public static double edgeValue(final ProblemImSeg image, final List<Set<Integer>> segments) throws Exception {
        double edgeValue = 0;
        for (Integer pixelIdx : IntStream.range(0, image.getPixelCount())
                                    .collect(ArrayList<Integer>::new, List::add, List::addAll)) {
            double pixelEdgeValue = 0;
            Graph.Edge[] neigborEdges = image.getEdgesToNeighbors(pixelIdx);
            for (Graph.Edge neighborEdge : neigborEdges) {
                // add 0 if in the same segment, distance otherwise
                Integer neighborIdx = neighborEdge.getToIndex();
                pixelEdgeValue += inSameSegment(segments, pixelIdx, neighborIdx) ? 0 : neighborEdge.getCost();
            }
            edgeValue += pixelEdgeValue;
        }
        return edgeValue;
    }

    public static double connectivityMeasure(final ProblemImSeg image, final List<Set<Integer>> segments) {
        return 0;
    }

    private static Pixel centroid(final ProblemImSeg image, final Set<Integer> segment) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for (Integer pixelIdx : segment) {
            Pixel pixel = image.getPixel(pixelIdx);
            red += pixel.getRed();
            green += pixel.getGreen();
            blue += pixel.getBlue();
        }
        int segmentSize = segment.size();
        red /= segmentSize;
        green /= segmentSize;
        blue /= segmentSize;

        return new Pixel(red, green, blue);
    }

    private static boolean inSameSegment(final List<Set<Integer>> segments,
                                         final Integer x, final Integer y) throws Exception {
        for (Set<Integer> segment : segments) {
            if (segment.contains(x)) {
                return segment.contains(y);
            } else if (segment.contains(y)) {
                return false;
            }
        }
        throw new Exception("Something is seriously wrong: segmentation does not contain x and y!");
    }
}
