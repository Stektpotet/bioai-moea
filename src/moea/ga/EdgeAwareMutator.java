package moea.ga;

import ga.RandomUtil;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.Set;

public class EdgeAwareMutator extends MutatorImSeg {

    private final ProblemImSeg problem;
    private static final ChromoImSeg.EdgeOut[] GENE_VARIANTS = ChromoImSeg.EdgeOut.values();
    public EdgeAwareMutator(ProblemImSeg problem, float pMutate) {
        super(pMutate);
        this.problem = problem;
    }

    @Override
    public ChromoImSeg mutate(ChromoImSeg chromosome) {
        var genotype = chromosome.cloneGenotype();

        var phenotype = chromosome.getPhenotype(problem);
        var targetOfMutation = RandomUtil.randomChoice(phenotype);
        final Set<Integer> all = targetOfMutation.getAll();

        int gene = RandomUtil.random.nextInt(all.size());
        int i = 0;
        for (Integer pid : all) {
            if (i == gene){
                int r = RandomUtil.random.nextInt(GENE_VARIANTS.length);
                r = (r + (genotype[gene] == GENE_VARIANTS[r] ? 1 : 0)) % GENE_VARIANTS.length;
                genotype[pid] = GENE_VARIANTS[r];
                break;
            }
            i++;
        }


//        var geneToInsert = RandomUtil.randomChoice(GENE_VARIANTS);
//
//        // If this is a splitting mutation, it should happen somewhere in "the middle" of the segment
//        if (geneToInsert == ChromoImSeg.EdgeOut.NONE) {
//            genotype[RandomUtil.randomChoice(targetOfMutation.getNonEdge())] = geneToInsert;
//            return new ChromoImSeg(genotype);
//        }
//        // Otherwise, the gene should ideally be put along the edge, and potentially join segments
//        genotype[RandomUtil.randomChoice(targetOfMutation.getEdge())] = geneToInsert;
        return new ChromoImSeg(genotype);
    }
}
