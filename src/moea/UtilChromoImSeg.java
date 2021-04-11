package moea;

import collections.Pixel;

import java.util.List;
import java.util.Set;

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

    public static double edgeValue(final ProblemImSeg image, final List<Set<Integer>> segments) {
        return 0;
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
}
