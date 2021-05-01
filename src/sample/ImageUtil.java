package sample;

import collections.Segment;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import moea.ProblemImSeg;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class ImageUtil {

    public static int[] traceSegments(final int[] rawImage, final Collection<Segment> segments) {
        return traceSegments(rawImage, segments, 0x00ff00);
    }
    public static int[] traceSegments(final int[] rawImage, final Collection<Segment> segments, final int color) {
        int[] tracedRawImage = rawImage.clone();
        for (Segment segment : segments) {
            for (var p : segment.getEdge()) {
                tracedRawImage[p] = color;
            }
        }
        return tracedRawImage;
    }
    public static void traceSegmentsOnto(final WritableImage img, final Collection<Segment> segments, final int color) {
        final int width = (int) img.getWidth();
        final int height = (int) img.getHeight();
        int[] buffer = new int[width * height];
        img.getPixelReader().getPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), buffer,
                0, width
        );

        img.getPixelWriter().setPixels(0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), traceSegments(buffer, segments, color),
                0, width
        );
    }
    public static void traceSegmentsOnto(final WritableImage img, final Collection<Segment> segments) {
        traceSegmentsOnto(img, segments, 0x00ff00);
    }

    public static void writeImage(final WritableImage img, final int[] content) {
        final int width = (int) img.getWidth();
        final int height = (int) img.getHeight();
        img.getPixelWriter().setPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), content, 0, width
        );
    }
    public static void clearImage(final WritableImage img) {
        final int width = (int) img.getWidth();
        final int height = (int) img.getHeight();
        img.getPixelWriter().setPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), new int[width * height], 0, width
        );
    }

    public static int[] readImage(final Image img) {
        final int width = (int) img.getWidth();
        final int height = (int) img.getHeight();
        int[] buffer = new int[width * height];
        img.getPixelReader().getPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), buffer, 0, width
        );
        return buffer;
    }

    public static void writeToFile(ProblemImSeg problem, int[] rgb_ints) throws IOException {
        DataBuffer rgbData = new DataBufferInt(rgb_ints, rgb_ints.length);

        WritableRaster raster = Raster.createPackedRaster(rgbData, problem.getWith(),
                problem.getPixelCount() / problem.getWith(), problem.getWith(),
                new int[]{0xff0000, 0xff00, 0xff}, null);

        ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

        BufferedImage img = new BufferedImage(colorModel, raster, false, null);

        String fname = "test.png";
        ImageIO.write(img, "png", new File(fname));
    }
}

