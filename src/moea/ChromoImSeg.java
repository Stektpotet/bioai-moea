package moea;

import collections.*;
import ga.RandomUtil;
import ga.data.Chromosome;

import java.util.*;
import java.util.stream.IntStream;

public class ChromoImSeg implements Chromosome<ProblemImSeg> {

    public static class Fitness implements Comparable<Fitness> {
        private final double edge, deviation, connectivity;

        private Fitness(double edge, double deviation, double connectivity) {
            this.edge = edge;
            this.deviation = deviation;
            this.connectivity = connectivity;
        }

        @Override
        public int compareTo(Fitness o) {
            if (this.connectivity <= o.connectivity && this.edge >= o.edge && this.deviation <= o.deviation) {
                return 1;
            } else if ((this.connectivity >= o.connectivity && this.edge <= o.edge && this.deviation >= o.deviation)) {
                return -1;
            }
            return 0;
        }

        public double getEdge() {
            return edge;
        }

        public double getDeviation() {
            return deviation;
        }

        public double getConnectivity() {
            return connectivity;
        }
    }


    private final EdgeOut[] genotype;
    private boolean phenoOutdated;
    private List<Segment> phenotype;
    private boolean fitnessOutdated;
    private Fitness fitness;

    private static final double WEIGHT_OVDEV = 2.0;     // PENALIZES SEGMENTS BEING DISSIMILAR IN COLOR
    private static final double WEIGHT_EDGE = 1.0;      // REWARDS HAVING FEW NEIGHBOURING PIXELS
    private static final double WEIGHT_CONNECT = 2.0;   // PENALIZES SEGMENTS ...?

    public ChromoImSeg(EdgeOut[] genotype) {
        this.genotype = genotype;
        this.phenoOutdated = true;
        this.fitnessOutdated = true;
    }

    public List<Segment> getPhenotype (ProblemImSeg problem) {
        if (phenoOutdated) {
            phenotype(problem);
            phenoOutdated = false;
            fitnessOutdated = true;
        }
        return phenotype;
    }

    public EdgeOut[] cloneGenotype() {
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
            fitness = calculateFitnessComponents(problem);
            fitnessOutdated = false;
        }
        return fitness.connectivity + fitness.deviation - fitness.edge;
    }

    public Fitness calculateFitnessComponents(ProblemImSeg problem) {
        if (phenoOutdated) {
            phenotype(problem);
            phenoOutdated = false;
            fitnessOutdated = true;
        }
        if (!fitnessOutdated) {
            return fitness;
        }
        Image image = problem.getImage();
        Graph graph = problem.getGraph();

        double deviation = 0.0;
        double edge = 0.0;
        double connectivity = 0.0;

        for (Segment segment : phenotype) {
            Set<Integer> allPixelsOfSegment = segment.getAll();
            Pixel centroid = segment.getCentroid();
            for (Integer pid : segment.getNonEdge()) {
                deviation += WEIGHT_OVDEV * image.getPixel(pid).distance(centroid);
            }
            for (Integer pid : segment.getEdge()) {
                for (Graph.Edge neighbour : graph.getAdjacent(pid)) {
                    if (!allPixelsOfSegment.contains(neighbour.getToIndex())) {
                        edge += WEIGHT_EDGE * neighbour.getCost();
                        connectivity += WEIGHT_CONNECT * 0.125;
                    }
                }
            }
        }
        fitnessOutdated = false;
        fitness = new Fitness(edge, deviation, connectivity);
        return fitness;
    }

    private List<Segment> phenotype(ProblemImSeg problem) {
        if (!phenoOutdated) {
            return phenotype;
        }

        RandomSet<Integer> unvisited = IntStream.range(0, genotype.length).collect(RandomSet::new, RandomSet::add, RandomSet::addAll);
        List<Set<Integer>> segmentation = new ArrayList<>();

        while (!unvisited.isEmpty()) {
            Integer element = unvisited.pollRandom(RandomUtil.random);
            unvisited.remove(element);

            Set<Integer> currentSegment = new HashSet<>();
            currentSegment.add(element);

            Integer predecessor;
            while (true) {
                predecessor = element;
                element = pointsTo(problem, predecessor);

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


        List<Segment> segments = new ArrayList<>(segmentation.size());
        for (var protoSegment : segmentation) {
            segments.add(new Segment(protoSegment, problem.getImage()));
        }

        phenotype = Collections.unmodifiableList(segments);
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
