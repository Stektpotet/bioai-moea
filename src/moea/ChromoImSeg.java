package moea;

import collections.ProblemImSeg;
import ga.data.Chromosome;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChromoImSeg implements Chromosome<ProblemImSeg> {

    private EdgeOut[] genes;
    private List<Set<Integer>> segments;

    public ChromoImSeg(ProblemImSeg image) {
        this.genes = new EdgeOut[image.getLength()];
    }

    @Override
    public double fitness(ProblemImSeg problemImSeg) {
        return 0;
    }

    private enum EdgeOut {
        RIGHT,
        LEFT,
        UP,
        DOWN,
        NONE
    }
}
