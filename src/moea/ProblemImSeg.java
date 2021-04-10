package moea;

import collections.Image;
import collections.Pixel;

public class ProblemImSeg {
    private final Image image;

    public ProblemImSeg(Image image) {
        this.image = image;
    }

    public final int getWith() {
        return image.getWidth();
    }

    public final Pixel getPixel(int flatIndex) {
        return image.getPixel(flatIndex);
    }
}
