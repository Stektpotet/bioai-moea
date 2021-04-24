package ga.data;

import moea.ProblemImSeg;

public interface Chromosome<Problem> {
    public double fitness (Problem problem) throws Exception;
}
