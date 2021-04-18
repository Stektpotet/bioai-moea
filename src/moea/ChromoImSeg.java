package moea;

import collections.Graph;
import ga.data.Chromosome;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ChromoImSeg implements Chromosome<ProblemImSeg> {

    private final EdgeOut[] genotype;
    private boolean phenoOutdated;
    private List<Set<Integer>> phenotype;
    private boolean fitnessOutdated;
    private double fitness;

    private static final double WEIGHT_OVDEV = 0.4;
    private static final double WEIGHT_EDGE = 0.3;
    private static final double WEIGHT_CONNECT = 0.3;

    public ChromoImSeg(EdgeOut[] genotype) {
        this.genotype = genotype;
        this.phenoOutdated = true;
        this.fitnessOutdated = true;
    }

    public List<Set<Integer>> getPhenotype (ProblemImSeg problem) throws Exception {
        if (phenoOutdated) {
            phenotype(problem);
        }
        return phenotype;
    }

    public EdgeOut[] getGenotype() {
        // TODO: find out how to make immutable
        return genotype.clone();
    }

    @Override
    public double fitness(ProblemImSeg problem) {
        if (phenoOutdated) {
            phenotype(problem);
            phenoOutdated = false;
            fitnessOutdated = true;
        }
        if (fitnessOutdated) {
            Function<Graph.Edge, Double> edgeValue = (edge) ->
                    UtilChromoImSeg.inSameSegment(phenotype, edge.getFromIndex(), edge.getToIndex()) ? 0 : edge.getCost();
            Function<Graph.Edge, Double> connectivity = (edge) ->
                    UtilChromoImSeg.inSameSegment(phenotype, edge.getFromIndex(), edge.getToIndex()) ? 0 : 0.125;

            // TODO: check / think about if a "-" is okay (we could instead normalize and subtract from one).
            //          - fitness could then get negative - is that okay for selectors (simple GA) and NSGA-II?
            //          - does logic of subtracting and minimizing work well together? (or: subtracting from one and weighting)
            Function<Graph.Edge, Double> aggregateEVandCperEdge =
                    (edge) -> - WEIGHT_EDGE * edgeValue.apply(edge) + WEIGHT_CONNECT * connectivity.apply(edge);

            double weightedOverallDev = WEIGHT_OVDEV * UtilChromoImSeg.overallDeviation(problem, phenotype);
            double weightedEdgeValueConnectivity = problem.sumMapOverEdges(aggregateEVandCperEdge);

            fitness = weightedOverallDev + weightedEdgeValueConnectivity;
            fitnessOutdated = false;
        }

        return fitness;
    }

    List<Set<Integer>> phenotype(ProblemImSeg image) {

        if (!phenoOutdated) {
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

                // Check if the segment does not already contain contain the element pointed to
                if (!currentSegment.add(element)) {
                    segments.add(currentSegment);
                    break;
                }
                // Otherwise, check if the pointed to element can be removed from unvisited,
                // if it cannot, then it is already in another segment
                else if (!unvisited.remove(element)) {
                    boolean terriblyWrong = true;
                    for (Set<Integer> segment : segments) {
                        if (segment.contains(element)) {
                            segment.addAll(currentSegment);
                            terriblyWrong = false;
                            break;
                        }
                    }
                    if (terriblyWrong) {
                        System.out.println("Something is terribly wrong");
                    }
                    break;
                } else {
                    currentSegment.add(element);
                }
            }
        }
        phenotype = segments;

        phenoOutdated = false;
        fitnessOutdated = true;
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
                return pointsTo >= genotype.length ? pixelIdx : pointsTo;
            case RIGHT:
                pointsTo = pixelIdx + 1;
                return pointsTo % width == 0 ? pixelIdx : pointsTo;
            case LEFT:
                pointsTo = pixelIdx - 1;
                return pixelIdx % width == 0 ? pixelIdx : pointsTo;
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
