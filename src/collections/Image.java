package collections;

public class Image {
    private final Pixel[] pixels;
    private final int width, height;
    public Image(final int[] argb, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Pixel[argb.length];
        for (int i = 0; i < argb.length; i++) {
            this.pixels[i] = new Pixel(
                    (byte) ((argb[i] >> 16) & 0xff),
                    (byte) ((argb[i] >> 8) & 0xff),
                    (byte) (argb[i] & 0xff)
                    // alpha component is argb[i] >> 24
            );
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public final Pixel getPixel(int flatIndex) {
        return pixels[flatIndex];
    }

    public final Pixel getPixel(int x, int y) {
        assert 0 <= x && x < width && 0 <= y && y < height;
        return pixels[width * y + x];
    }
}
