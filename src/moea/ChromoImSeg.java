package moea;

import collections.*;
import ga.RandomUtil;
import ga.data.Chromosome;

import java.util.*;
import java.util.stream.IntStream;

public class ChromoImSeg implements Chromosome<ProblemImSeg> {

    private final EdgeOut[] genotype;
    private boolean phenoOutdated;
    private List<Segment> phenotype;
    private boolean fitnessOutdated;
    private double fitness;

    private static final double WEIGHT_OVDEV = 0.4;
    private static final double WEIGHT_EDGE = 0.1;
    private static final double WEIGHT_CONNECT = 1.0;

    public ChromoImSeg(EdgeOut[] genotype) {
        this.genotype = genotype;
        this.phenoOutdated = true;
        this.fitnessOutdated = true;
    }

    public List<Segment> getPhenotype (ProblemImSeg problem) throws Exception {
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
            fitnessOutdated = true;
        }
        if (fitnessOutdated) {
            Image image = problem.getImage();
            Graph graph = problem.getGraph();
            double sum = 0.0;
//            double sum = phenotype.parallelStream().mapToDouble(s -> {
//                double segSum = 0.0;
//                Pixel centroid = UtilChromoImSeg.centroid(image, s);
//                for (Integer pixelIndex : s) {
//                    // calculate edge value and connectivy (same control flow)
//                    segSum += graph.streamValidNeighbours(pixelIndex).mapToDouble(e -> edgeOrConnectivity(s, e)).sum();
//
//                    // calculate overall deviation
//                    segSum += WEIGHT_OVDEV * image.getPixel(pixelIndex).distance(centroid);
//                }
//                return segSum;
//            }).sum();

            for (Segment segment : phenotype) {
//                Set<Integer> all = segment.getAll();
//                for (Integer pixelIndex : all) {
//                    // calculate edge value and connectivy (same control flow)
//                    for (Graph.Edge neighbour : graph.getCardinals(pixelIndex)) {
//                        sum += edgeOrConnectivity(all, neighbour);
//                    }
//                    // calculate overall deviation
//                    sum += WEIGHT_OVDEV * image.getPixel(pixelIndex).distance(segment.getCentroid());
//                }
                for (Integer pid : segment.getNonEdge()) {
                    for (Graph.Edge neighbour : graph.getCardinals(pid)) {
                        sum += WEIGHT_CONNECT * neighbour.getCost();
                    }
                    sum += WEIGHT_OVDEV * image.getPixel(pid).distance(segment.getCentroid());
                }
                for (Integer pid : segment.getEdge()) {
                    sum -= WEIGHT_EDGE * 0.125;
                    sum += WEIGHT_OVDEV * image.getPixel(pid).distance(segment.getCentroid());
                }
            }
            fitness = sum;
            fitnessOutdated = false;
        }
        return fitness;
    }

    private double edgeOrConnectivity(Set<Integer> segment, Graph.Edge e) {
        if (segment.contains(e.getToIndex())) {
            return WEIGHT_CONNECT * e.getCost();
        }
        return - WEIGHT_EDGE * 0.125;
    }

//    @Override
//    public double fitness(ProblemImSeg problem) {
//        if (phenoOutdated) {
//            phenotype(problem);
//            phenoOutdated = false;
//            fitnessOutdated = true;
//        }
//        if (fitnessOutdated) {
//            Function<Graph.Edge, Double> edgeValue = (edge) ->
//                    UtilChromoImSeg.inSameSegment(phenotype, edge.getFromIndex(), edge.getToIndex()) ? 0 : edge.getCost();
//            Function<Graph.Edge, Double> connectivity = (edge) ->
//                    UtilChromoImSeg.inSameSegment(phenotype, edge.getFromIndex(), edge.getToIndex()) ? 0 : 0.125;
//
//            // TODO: check / think about if a "-" is okay (we could instead normalize and subtract from one).
//            //          - fitness could then get negative - is that okay for selectors (simple GA) and NSGA-II?
//            //          - does logic of subtracting and minimizing work well together? (or: subtracting from one and weighting)
//            Function<Graph.Edge, Double> aggregateEVandCperEdge =
//                    (edge) -> - WEIGHT_EDGE * edgeValue.apply(edge) + WEIGHT_CONNECT * connectivity.apply(edge);
//
//            double weightedOverallDev = WEIGHT_OVDEV * UtilChromoImSeg.overallDeviation(problem.getImage(), phenotype);
//            double weightedEdgeValueConnectivity = problem.sumMapOverEdges(aggregateEVandCperEdge);
//
//            fitness = weightedOverallDev + weightedEdgeValueConnectivity;
//            fitnessOutdated = false;
//        }
//
//        return fitness;
//    }

    List<Segment> phenotype(ProblemImSeg image) {

        if (!phenoOutdated) {
            return phenotype;
        }

        RandomSet<Integer> unvisited = IntStream.range(0, genotype.length).collect(RandomSet::new, RandomSet::add, RandomSet::addAll);
//        List<Segment> segments = new ArrayList<>();
        List<Set<Integer>> segmentation = new ArrayList<>();

        while (!unvisited.isEmpty()) {
            Integer element = unvisited.pollRandom(RandomUtil.random);
            unvisited.remove(element);

            Set<Integer> currentSegment = new HashSet<>();
            currentSegment.add(element);

            Integer predecessor;
            while (true) {
                predecessor = element;
                element = pointsTo(image, predecessor);

                // Check if the segment does not already contain contain the element pointed to
                if (!currentSegment.add(element)) {
                    segmentation.add(currentSegment);
                    break;
                }
                // Otherwise, check if the pointed to element can be removed from unvisited,
                // if it cannot, then it is already in another segment
                else if (!unvisited.remove(element)) {
                    boolean terriblyWrong = true;
                    for (Set<Integer> segment : segmentation) {
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


        phenotype = Collections.unmodifiableList(
                segmentation.stream().map(s -> new Segment(s, image.getImage())).toList()
        );
        phenoOutdated = false;
        fitnessOutdated = true;
        return phenotype;
    }

    private int pointsTo(ProblemImSeg image, int pixelIdx) {
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
