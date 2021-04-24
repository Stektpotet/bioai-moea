package collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Segment {
    private final Set<Integer> all;
    private final Set<Integer> edge;
    private final Set<Integer> nonEdge;
    private final Pixel centroid;

    public Segment(Set<Integer> pixelIdx, Image img) {
        all = pixelIdx;
        int[] moores = new int[]{
                -img.getWidth() - 1, -img.getWidth(), -img.getWidth() + 1,
                -1,                                    1,
                img.getWidth() - 1,   img.getWidth(), img.getWidth() + 1
        };
        edge = new HashSet<>();
        nonEdge = new HashSet<>(pixelIdx.size());
        int[] avgColor = new int[3];
        for (Integer pid : all) {
            Pixel p = img.getPixel(pid);
            avgColor[0] += p.getRed();
            avgColor[1] += p.getGreen();
            avgColor[2] += p.getBlue();

            boolean isEdge = false;
            for (int mn : moores) {
                //TODO: check if on the same side of the image!
                if (!pixelIdx.contains(pid + mn)) {
                    isEdge = true;
                    break;
                }
            }
            if (isEdge) {
                edge.add(pid);
            } else {
                nonEdge.add(pid);
            }
        }
        for (int i = 0; i < 3; i++) {
            avgColor[i] /= all.size();
        }
        centroid = new Pixel(avgColor[0], avgColor[1], avgColor[2]);
    }

    // TODO: Make Unmodifiable
    public Set<Integer> getAll() {
        return Collections.unmodifiableSet(all);
    }

    public Set<Integer> getEdge() {
        return Collections.unmodifiableSet(edge);
    }

    public Set<Integer> getNonEdge() {
        return Collections.unmodifiableSet(nonEdge);
    }

    public Pixel getCentroid() {
        return centroid;
    }
}
