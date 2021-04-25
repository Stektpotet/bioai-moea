package sample;

import collections.Segment;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

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
}

