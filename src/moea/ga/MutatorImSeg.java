package moea.ga;

import ga.RandomUtil;
import ga.change.Mutator;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

public class MutatorImSeg implements Mutator<ProblemImSeg, ChromoImSeg> {

    private final float pMutate;
    private static final ChromoImSeg.EdgeOut[] GENE_VARIANTS = ChromoImSeg.EdgeOut.values();
    public MutatorImSeg(float pMutate) {
        this.pMutate = pMutate;
    }

    @Override
    public ChromoImSeg mutate(ChromoImSeg chromosome) {
        ChromoImSeg.EdgeOut[] genotype = chromosome.getGenotype();
        if (RandomUtil.random.nextFloat() < this.pMutate) {
            genotype[RandomUtil.random.nextInt(genotype.length)] = RandomUtil.randomChoice(GENE_VARIANTS);
            return new ChromoImSeg(genotype);
        }
        return chromosome;
    }
}
