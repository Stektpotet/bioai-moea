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
import java.util.List;

public class ImageUtil {

    public static int[] traceSegmentsOnto(final int[] rawImage, final Collection<Segment> segments) {
        return traceSegmentsOnto(rawImage, segments, 0x00ff00);
    }
    public static int[] traceSegmentsOnto(final int[] rawImage, final Collection<Segment> segments, final int color) {
        final int[] tracedRawImage = rawImage.clone();
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
                PixelFormat.getIntArgbPreInstance(), traceSegmentsOnto(buffer, segments, color),
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

    public static void writeToFile(final Image image, final String path) throws IOException {
        final int[] rgbInts = readImage(image);
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final DataBuffer rgbData = new DataBufferInt(rgbInts, rgbInts.length);

        final WritableRaster raster = Raster.createPackedRaster(rgbData, width,
                height, width,
                new int[]{0xff0000, 0xff00, 0xff}, null);

        final ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

        final BufferedImage img = new BufferedImage(colorModel, raster, false, null);

        ImageIO.write(img, "png", new File(path));
    }

    public static void writeFrontToFiles(final String pathToFolder, final Image[] front) throws IOException {
        for (int i = 0; i < front.length; i++) {
            writeToFile(front[i], pathToFolder + i + ".png");
        }
    }
}

