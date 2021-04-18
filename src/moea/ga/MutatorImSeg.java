package moea.ga;

import ga.RandomUtil;
import ga.change.Mutator;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

public class MutatorImSeg implements Mutator<ProblemImSeg, ChromoImSeg> {

    private final float pMutate;
    private static final ChromoImSeg.EdgeOut[] geneVariants = ChromoImSeg.EdgeOut.values();
    public MutatorImSeg(float pMutate) {
        this.pMutate = pMutate;
    }

    @Override
    public ChromoImSeg mutate(ChromoImSeg chromosome) {
        ChromoImSeg.EdgeOut[] genotype = chromosome.getGenotype();
        for (int i = 0; i < genotype.length; i++) {
            if (RandomUtil.random.nextFloat() < this.pMutate)
                genotype[i] = RandomUtil.randomChoice(geneVariants);
        }
        return new ChromoImSeg(genotype);
    }
}
