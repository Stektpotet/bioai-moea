package moea.ga;

import ga.RandomUtil;
import ga.change.Mutator;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.ArrayList;
import java.util.List;

public class MutatorImSeg implements Mutator<ProblemImSeg, ChromoImSeg> {

    private final float pMutate;
    private static final ChromoImSeg.EdgeOut[] GENE_VARIANTS = ChromoImSeg.EdgeOut.values();
    public MutatorImSeg(float pMutate) {
        this.pMutate = pMutate;
    }

    @Override
    public List<ChromoImSeg> mutateAll(List<ChromoImSeg> chromosomes) {
        List<ChromoImSeg> mutated = new ArrayList<>(chromosomes.size());
        for (ChromoImSeg c : chromosomes) {
            mutated.add((RandomUtil.random.nextFloat() < this.pMutate) ? mutate(c) : c);
        }
        return mutated;
    }

    @Override
    public ChromoImSeg mutate(ChromoImSeg chromosome) {
        ChromoImSeg.EdgeOut[] genotype = chromosome.cloneGenotype();
        int gene = RandomUtil.random.nextInt(genotype.length);
        int r = RandomUtil.random.nextInt(GENE_VARIANTS.length);
        r = (r + (genotype[gene] == GENE_VARIANTS[r] ? 1 : 0)) % GENE_VARIANTS.length;
        genotype[gene] = GENE_VARIANTS[r];
        return new ChromoImSeg(genotype);
    }

    /*

    l = [a,b,c]

    g = c
    r = 2
    r = r + (l[r]==c ? 1:0) % len(l)

    r -> 1


     */
}
