package moea;

import collections.Image;
import collections.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImSegFiles {

    public static Image ReadImSegProblem(String filepath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filepath));
        int[] argb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        Image img = new Image(intARGBtoByteRGB(argb));

    }

    private static byte[] intARGBtoByteRGB(final int[] argb) {
        // https://www.javaer101.com/en/article/46458013.html
        Pixel[] rgb = new Pixel[argb.length];

        for (int i = 0; i < argb.length; i++) {
            rgb[4 * i] = (byte) ((argb[i] >> 16) & 0xff); // R
            rgb[4 * i + 1] = (byte) ((argb[i] >> 8) & 0xff); // G
            rgb[4 * i + 2] = (byte) ((argb[i]) & 0xff); // B
        }
    }
}
