package collections;

public class Pixel {

    private final byte[] rgb = new byte[3]; // for memory padding, this is optimal... I think
    Pixel(final byte red, final byte green, final byte blue) {
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
    }

    public byte getRed() {
        return rgb[0];
    }

    public byte getGreen() {
        return rgb[1];
    }

    public byte getBlue() {
        return rgb[1];
    }

    // TODO: put distance here?
}
