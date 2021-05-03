package moea.ga;
import ga.RandomUtil;
import ga.change.Recombinator;
import moea.ChromoImSeg;
import moea.ProblemImSeg;
import moea.UtilChromoImSeg;

import java.util.*;

public class SegmentationCrossover implements Recombinator<ChromoImSeg> {

    private final float pCrossover;
    private final int k;
    private final ProblemImSeg problem;

    public SegmentationCrossover(float pCrossover, int k, ProblemImSeg problem) {
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
        List<ChromoImSeg>  children = new ArrayList<>(2);
        if (UtilChromoImSeg.hammingDistance(mum, dad) == 0) {
//            System.out.println("mum is dad!");
            children.add(mum);
            children.add(dad);
            return children;
        }
//        if (RandomUtil.random.nextFloat() < 0.3) {
//            return kPointCrossover(mum, dad);
//        }
//        var nonIntersectingSets= getNonIntersectingSegments(mum, dad);
//        if (nonIntersectingSets.size() == 0) {
//            return uniformCrossover(mum, dad);
//        }
//
//        ChromoImSeg.EdgeOut[] genoDau = mum.cloneGenotype();
//        ChromoImSeg.EdgeOut[] genoSon = dad.cloneGenotype();
//        for(var p : nonIntersectingSets.get(0)) { // Put the segment from mum into son
//            genoSon[p] = genoDau[p];
//        }
//        for(var p : nonIntersectingSets.get(1)) { // Put the segment from dad into daughter
//            genoDau[p] = genoSon[p];
//        }
        var mumSegment = RandomUtil.randomChoice(mum.getPhenotype(problem));
        var dadSegment = RandomUtil.randomChoice(dad.getPhenotype(problem));

        ChromoImSeg.EdgeOut[] genoMum = mum.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoDad = dad.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoDau = mum.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoSon = dad.cloneGenotype();
        for(var p : mumSegment.getAll()) { // Put the segment from mum into son
            genoSon[p] = genoMum[p];
        }
        for(var p : dadSegment.getAll()) { // Put the segment from dad into daughter
            genoDau[p] = genoDad[p];
        }

        children.add(new ChromoImSeg(genoDau));
        children.add(new ChromoImSeg(genoSon));
        return children;
    }

    private List<ChromoImSeg> uniformCrossover(ChromoImSeg mum, ChromoImSeg dad) {
        ChromoImSeg.EdgeOut[] genoMum = mum.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoDad = dad.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoDau = mum.cloneGenotype();
        ChromoImSeg.EdgeOut[] genoSon = dad.cloneGenotype();

        for (int i = 0; i < genoMum.length; i++) {
            if (RandomUtil.random.nextFloat() > 0.5)
                continue;
            genoSon[i] = genoMum[i];
            genoDau[i] = genoDad[i];
        }
        List<ChromoImSeg>  children = new ArrayList<>(2);
        children.add(new ChromoImSeg(genoDau));
        children.add(new ChromoImSeg(genoSon));
        return children;
    }

    private List<ChromoImSeg> kPointCrossover(ChromoImSeg mum, ChromoImSeg dad) {
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


    List<Set<Integer>> getNonIntersectingSegments(ChromoImSeg mum, ChromoImSeg dad) {
        var phenoMum = mum.getPhenotype(problem);
        var phenoDad = dad.getPhenotype(problem);

        for (var mSeg : phenoMum) {
            for (var dSeg : phenoDad) {
                // We only need to see if the edges intersect
                if(mSeg.getEdge().stream().noneMatch(dSeg.getEdge()::contains)) {
                    return List.of(mSeg.getAll(), dSeg.getAll());
                }
            }
        }
        return List.of();
    }

}
