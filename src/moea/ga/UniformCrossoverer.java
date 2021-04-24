package moea.ga;

import ga.RandomUtil;
import ga.change.Recombinator;
import moea.ChromoImSeg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UniformCrossoverer implements Recombinator<ChromoImSeg> {

    private final float pCrossover;

    public UniformCrossoverer(float pCrossover) {
        this.pCrossover = pCrossover;
    }

    @Override
    public List<ChromoImSeg> recombine(List<ChromoImSeg> parents) throws Exception {
        if (parents.size() % 2 != 0) {
            throw new Exception("Odd number of parents (someone ends up having sex with him/herself)!");
        }

        Iterator<ChromoImSeg> parentsIter = parents.listIterator();
        List<ChromoImSeg> children = new ArrayList<>(parents.size());
        while (parentsIter.hasNext()) {
            ChromoImSeg mum = parentsIter.next();
            ChromoImSeg dad = parentsIter.next();
            if (RandomUtil.random.nextFloat() <= pCrossover) {
                children.addAll(crossover(mum, dad));
            } else {
                children.add(mum);
                children.add(dad);
            }
        }

        return children;
    }

    @Override
    public List<ChromoImSeg> crossover(final ChromoImSeg mum, final ChromoImSeg dad) {
        int geneCount = mum.cloneGenotype().length;

        final ChromoImSeg.EdgeOut[] mumGenes = mum.cloneGenotype();
        final ChromoImSeg.EdgeOut[] dadGenes = dad.cloneGenotype();
        ChromoImSeg.EdgeOut[] dauGenes = new ChromoImSeg.EdgeOut[geneCount];
        ChromoImSeg.EdgeOut[] sonGenes = new ChromoImSeg.EdgeOut[geneCount];

        for (int i = 0; i < geneCount; i++) {
            ChromoImSeg.EdgeOut mumGene = mumGenes[i];
            ChromoImSeg.EdgeOut dadGene = dadGenes[i];

            if (RandomUtil.random.nextFloat() > 0.5) {
                dauGenes[i] = mumGene;
                sonGenes[i] = dadGene;
            } else {
                dauGenes[i] = dadGene;
                sonGenes[i] = mumGene;
            }
        }

        ChromoImSeg dau = new ChromoImSeg(dauGenes);
        ChromoImSeg son = new ChromoImSeg(sonGenes);

        List<ChromoImSeg> children = new ArrayList<>();
        children.add(dau);
        children.add(son);

        return children; //replaced array list creation
    }
}
