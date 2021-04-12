package moea;

import collections.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImSegFiles {
    public static ProblemImSeg ReadImSegProblem(String filepath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int[] argb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        return new ProblemImSeg(new Image(argb, image.getWidth(), image.getHeight()));
    }
}
