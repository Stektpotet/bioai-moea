package moea;

import collections.Graph;
import collections.Pixel;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
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

    public static Comparator<ChromoImSeg> chromosomeFitnessComparator(ProblemImSeg problem) {
        return (a, b) -> (int) Math.signum(a.fitness(problem) - b.fitness(problem));
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

    public static boolean inSameSegment(final List<Set<Integer>> segments,
                                         final Integer x, final Integer y) {
        for (Set<Integer> segment : segments) {
            boolean containsY = segment.contains(y);
            if (segment.contains(x))
                return containsY;
            if (containsY)
                return false;
        }

//        for (Set<Integer> segment : segments) {
//            if (segment.contains(x)) {
//                return segment.contains(y);
//            } else if (segment.contains(y)) {
//                return false;
//            }
//        }
        System.out.println("This is really annoying and due to the fact that evaluation in Java isn't lazy, for" +
                "understanding the problem see ChromoImSeg.fitness()");
        return false;
        // throw new Exception("Something is seriously wrong: segmentation does not contain x and y!");
    }
}
