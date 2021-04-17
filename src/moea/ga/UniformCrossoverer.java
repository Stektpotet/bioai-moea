package moea.ga;

import ga.RandomUtil;
import ga.change.Recombinator;
import moea.ChromoImSeg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UniformCrossoverer implements Recombinator<ChromoImSeg> {

    @Override
    public List<ChromoImSeg> recombine(List<ChromoImSeg> parents) throws Exception {
        if (parents.size() % 2 != 0) {
            throw new Exception("Odd number of parents (someone ends up having sex with him/herself)!");
        }

        Iterator<ChromoImSeg> parentsIter = parents.listIterator();
        List<ChromoImSeg> children = new ArrayList<>(parents.size());
        while (parentsIter.hasNext()) {
            children.addAll(crossover(parentsIter.next(), parentsIter.next()));
        }

        return children;
    }

    @Override
    public List<ChromoImSeg> crossover(final ChromoImSeg mum, final ChromoImSeg dad) {
        int geneCount = mum.getGenotype().length;

        final ChromoImSeg.EdgeOut[] mumGenes = mum.getGenotype();
        final ChromoImSeg.EdgeOut[] dadGenes = dad.getGenotype();
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

        List<ChromoImSeg> children = new ArrayList<>(2);
        children.add(dau);
        children.add(son);

        return children;
    }
}
