package collections;

public class Image {

    private final byte[] rawImage;
    private final Pixel[] pixels;
    private final int width, height;
    public Image(final int[] argb, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Pixel[argb.length];
        this.rawImage = new byte[argb.length * 3];
        for (int i = 0; i < argb.length; i++) {
            Pixel p = new Pixel(
                    (byte) ((argb[i] >> 16) & 0xff),
                    (byte) ((argb[i] >> 8) & 0xff),
                    (byte) (argb[i] & 0xff)
                    // alpha component is argb[i] >> 24
            );
            this.pixels[i] = p;
            this.rawImage[i * 3] = p.getRed();
            this.rawImage[i * 3 + 1] = p.getGreen();
            this.rawImage[i * 3 + 2] = p.getBlue();
        }
    }

    public byte[] getRawImage() {
        return rawImage;
    }

    public int getPixelCount() {
        return pixels.length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getDistance(int ia, int ib) {
        return pixels[ia].distance(pixels[ib]);
    }

    public final Pixel getPixel(int flatIndex) {
        return pixels[flatIndex];
    }

    public final Pixel getPixel(int x, int y) {
        assert 0 <= x && x < width && 0 <= y && y < height;
        return pixels[width * y + x];
    }
}
