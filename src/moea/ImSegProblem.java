package moea;

import collections.Image;
import collections.Pixel;

public class ImSegProblem {
    private final Image image;

    public ImSegProblem(Image image) {
        this.image = image;
    }

    public final Pixel getPixel(int flatIndex) {
        return image.getPixel(flatIndex);
    }
}
