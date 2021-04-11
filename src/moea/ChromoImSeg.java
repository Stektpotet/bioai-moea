package moea;

import ga.data.Chromosome;

import java.util.*;
import java.util.stream.IntStream;

public class ChromoImSeg implements Chromosome<ProblemImSeg> {

    private EdgeOut[] genotype;
    private boolean changed;
    private List<Set<Integer>> phenotype;

    public ChromoImSeg(ProblemImSeg image) {
        this.genotype = new EdgeOut[image.getPixelCount()];
        this.changed = true;
    }

    @Override
    public double fitness(ProblemImSeg problemImSeg) {
        return 0;
    }
    
    List<Set<Integer>> phenotype(ProblemImSeg image) throws Exception {

        if (!changed) {
            return phenotype;
        }

        Set<Integer> unvisited = IntStream.range(0, genotype.length).collect(HashSet::new, Set::add, Set::addAll);
        List<Set<Integer>> segments = new ArrayList<>();

        while (!unvisited.isEmpty()) {
            Integer element = unvisited.stream().findAny().get();
            unvisited.remove(element);

            Set<Integer> currentSegment = new HashSet<>();
            currentSegment.add(element);

            Integer predecessor;
            while (true) {
                predecessor = element;
                element = pointsTo(image, predecessor);

                if (!currentSegment.add(element)) {
                    segments.add(currentSegment);
                    break;
                } else if (unvisited.remove(element)) {
                    boolean terriblyWrong = true;
                    for (Set<Integer> segment : segments) {
                        if (segment.contains(element)) {
                            segment.addAll(currentSegment);
                            terriblyWrong = false;
                            break;
                        }
                    }
                    if (terriblyWrong) {
                        throw new Exception("Element was in visited, but not in any segment!");
                    }
                    break;
                } else {
                    currentSegment.add(element);
                }
            }
        }
        phenotype = segments;
        return Collections.unmodifiableList(segments);
    }

    private Integer pointsTo(ProblemImSeg image, Integer pixelIdx) {
        EdgeOut direction = genotype[pixelIdx];
        int width = image.getWith();

        int pointsTo;
        switch (direction) {
            case UP:
                pointsTo = pixelIdx - width;
                return pointsTo < 0 ? pixelIdx : pointsTo;
            case DOWN:
                pointsTo = pixelIdx + width;
                return pointsTo > genotype.length ? pixelIdx : pointsTo;
            case RIGHT:
                return pixelIdx % width == 0 ? pixelIdx : pixelIdx + 1;
            case LEFT:
                pointsTo = pixelIdx - 1;
                return pointsTo % width == 0 ? pixelIdx : pointsTo;
            default:
                return pixelIdx;
        }
    }

    public enum EdgeOut {
        RIGHT,
        LEFT,
        UP,
        DOWN,
        NONE
    }
}
