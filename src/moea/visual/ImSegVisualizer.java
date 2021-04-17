package moea.visual;

import collections.Pixel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import moea.ProblemImSeg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ImSegVisualizer {

    public ImSegVisualizer(int width, int height, GraphicsContext graphics) {
        this.width = width;
        this.height = height;
        this.graphics = graphics;
    }

    private class DrawOptions {
        public static Color backgroundColor = Color.valueOf("2a2e2e");
        public static Color depotColor = Color.valueOf("f16");
        public static Color customerColor = Color.valueOf("3ddb0d");
        public static Color customerColorMaxDemand = Color.valueOf("750505");
        public static double customerSize = 1.0;
        public static double depotSize = 2;
    }

    private final int width, height;
    private final GraphicsContext graphics;

    public void drawGraph(ProblemImSeg problem) {
        collections.Image problemImage = problem.getImage();
        graphics.getPixelWriter().setPixels(
                0,0, problemImage.getWidth(), problemImage.getHeight(),
                PixelFormat.getByteRgbInstance(), problemImage.getRawImage(), 0, problemImage.getWidth()*3
        );
    }
}
