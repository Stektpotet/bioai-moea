package ga;

import collections.RandomSet;
import moea.ChromoImSeg;

import java.util.*;

public final class RandomUtil {
    private RandomUtil(){} // prevents instantiation

    public static Random random = new Random(69);

    public static <T> T randomChoice(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

//    public static <T> T randomChoice(Set<T> set) {
//        int selectionIndex = random.nextInt(set.size());
//        var iter = set.iterator();
//        for (int i = 0; i < selectionIndex; i++) {
//            iter.next();
//        }
//        return iter.next();
//    }

    public static <T> T randomChoice(RandomSet<T> set) {
        return set.get(random.nextInt(set.size()));
    }

    public static <T> T randomChoiceRemove(List<T> list) {
        return list.remove(random.nextInt(list.size()));
    }

    public static <T> List<T> randomChoice(final List<T> list, int n, boolean replace) {
        assert replace || n <= list.size();

        if (replace) {
            List<T> chosen = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                chosen.add(list.get(random.nextInt(list.size())));
            }
            return chosen;
        }
        List<T> chosen = new ArrayList<>(list);
        Collections.shuffle(chosen, random);
        return chosen.subList(0, n);
    }

    public static <T> T randomChoice(T[] arr) {
        return arr[random.nextInt(arr.length)];
    }
}