package collections;

public class Pixel {

    private final byte[] rgb; // for memory padding, this is optimal... I think
    Pixel(final byte red, final byte green, final byte blue) {
        rgb = new byte[]{red, green, blue};
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

    public double distance(Pixel other) {
        float r = (rgb[0] - other.rgb[0]) / 255F;
        float g = (rgb[1] - other.rgb[1]) / 255F;
        float b = (rgb[2] - other.rgb[2]) / 255F;
        return Math.sqrt(r*r + g*g + b*b);
    }
}
