package collections;

public class Pixel {

    private final byte red;
    private final byte green;
    private final byte blue;

    public Pixel(final byte red, final byte green, final byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }
}
