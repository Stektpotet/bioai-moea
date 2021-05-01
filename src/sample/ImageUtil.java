package sample;

import collections.Segment;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class ImageUtil {


    public static int[] traceSegments(final int[] rawImage, final Collection<Segment> segments) {
        return traceSegments(rawImage, segments, 0x00ff00);
    }


    public static int[] traceSegments(final int[] rawImage, final Collection<Segment> segments, final int color) {
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
    public static void fillImage(final WritableImage img, int color) {
        final int width = (int) img.getWidth();
        final int height = (int) img.getHeight();
        final int[] buffer = new int[width * height];
        Arrays.fill(buffer, color);
        img.getPixelWriter().setPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), buffer, 0, width
        );
    }

    public static int[] readImageRaw(final Image img) {
        final int width = (int) img.getWidth();
        final int height = (int) img.getHeight();
        final int[] buffer = new int[width * height];
        img.getPixelReader().getPixels(
                0, 0, width, height,
                PixelFormat.getIntArgbPreInstance(), buffer, 0, width
        );
        return buffer;
    }

    public static void writeToFile(final Image image, final String directoryPath, final String name) throws IOException {
        final int[] rgbInts = readImageRaw(image);
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final DataBuffer rgbData = new DataBufferInt(rgbInts, rgbInts.length);

        final WritableRaster raster = Raster.createPackedRaster(rgbData, width,
                height, width,
                new int[]{0xff0000, 0xff00, 0xff}, null);

        final ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

        final BufferedImage img = new BufferedImage(colorModel, raster, false, null);
        final File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        ImageIO.write(img, "png", new File(directoryPath + name));
    }

    public static void writeFrontToFiles(final String pathToFolder, final Image[] front) throws IOException {
        for (int i = 0; i < front.length; i++) {
            writeToFile(front[i], pathToFolder, "pareto_" + i + ".png");
        }
    }
}

