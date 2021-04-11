package ga.data;

public interface Chromosome<Problem> {
    public double fitness (Problem problem) throws Exception;
}
