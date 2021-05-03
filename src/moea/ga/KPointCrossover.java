package moea.ga;
import ga.RandomUtil;
import ga.change.Recombinator;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KPointCrossover implements Recombinator<ChromoImSeg> {

    private final float pCrossover;
    private final int k;
    private final ProblemImSeg problem;

    public KPointCrossover(float pCrossover, int k, ProblemImSeg problem) {
        this.pCrossover = pCrossover;
        this.k = k;
        this.problem = problem;
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
        int[] points = RandomUtil.random.ints(0, problem.getPixelCount()).limit(k).toArray();

        ChromoImSeg.EdgeOut[] genoMum = mum.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoDad = dad.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoDau = new ChromoImSeg.EdgeOut[genoMum.length];
        ChromoImSeg.EdgeOut[] genoSon = new ChromoImSeg.EdgeOut[genoDad.length];

        int last = 0;
        ChromoImSeg.EdgeOut[] temp;
        for (int point : points) {
            for (int index = last; index < point; index++) {
                genoDau[index] = genoMum[index];
                genoSon[index] = genoDad[index];
            }
            last = point;

            // switching who gets genes from which parent
            temp = genoDau;
            genoDau = genoSon;
            genoSon = temp;
        }
        for (int index = last; index < genoMum.length; index++) {
            genoDau[index] = genoDad[index];
            genoSon[index] = genoMum[index];
        }

        List<ChromoImSeg>  children = new ArrayList<>(2);
        children.add(new ChromoImSeg(genoDau));
        children.add(new ChromoImSeg(genoSon));
        return children;
    }
}
