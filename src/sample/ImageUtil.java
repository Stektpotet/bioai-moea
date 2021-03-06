package sample;

import collections.Segment;
import evaluator.Evaluator;
import javafx.concurrent.Task;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import moea.ChromoImSeg;
import moea.ProblemImSeg;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ImageUtil {


    public static final String PATH_BLACK_WHITE = "type2/";
    public static final String PATH_COLOR_GREEN = "type1/";

    public static final int WHITE = 0xffffffff;
    public static final int BLACK = 0x00000000;
    public static final int GREEN = 0x0000ff00;

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


    public static void writeToFile(final int[] rgbInts, int width, int height, final String directoryPath, final String name) throws IOException {
        final DataBuffer rgbData = new DataBufferInt(rgbInts, rgbInts.length);

        final WritableRaster raster = Raster.createPackedRaster(rgbData, width,
                height, width,
                new int[]{0xff0000, 0xff00, 0xff}, null);

        final ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);

        final BufferedImage img = new BufferedImage(colorModel, raster, false, null);
        ensurePathExists(directoryPath);
        ImageIO.write(img, "png", new File(directoryPath + name));
    }

    private static void ensurePathExists(String directoryPath) {
        final File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void deleteRecursively(final String pathToFolder) {
        final File directory = new File(pathToFolder);
        deleteRecursively(directory);
    }

    public static boolean deleteRecursively(File directory) {
        File[] content = directory.listFiles();
        if (content != null) {
            for (File entry: content) {
                deleteRecursively(entry);
            }
        }
        return directory.delete();
    }

    public static final class Output extends Task<PRIOptimum> {

        final List<ChromoImSeg> front;
        final ProblemImSeg problem;
        final String outputDir;
        final String optDir;

        public Output(final List<ChromoImSeg> front, final ProblemImSeg problem,
                      final String outputDir, final String optDir) {
            this.front = front;
            this.problem = problem;
            this.outputDir = outputDir;
            this.optDir = optDir;
        }

        @Override
        protected PRIOptimum call() throws Exception {
            collections.Image image = problem.getImage();
            int width = image.getWidth();
            int height = image.getHeight();
            List<List<Segment>> phenotypes =
                    front.stream().map(c -> c.getPhenotype(problem)).collect(Collectors.toList());

            saveInBlackWhite(outputDir, width, height, phenotypes);
            saveInColorGreen(outputDir, image, width, height, phenotypes);

            // calculate PRIs
            Evaluator evaluator = new Evaluator(optDir, outputDir + PATH_BLACK_WHITE);
            double[] PRIs = evaluator.runSameThread();

            // save info on optimum and return
            PRIOptimum optimum = saveInfo(front, problem, outputDir, phenotypes, PRIs);
            return optimum;
        }

        private static PRIOptimum saveInfo(List<ChromoImSeg> front, ProblemImSeg problem, String directory,
                                           List<List<Segment>> phenotypes, double[] PRIs) throws IOException {
            // calculate objectives
            ChromoImSeg.Fitness[] objectives = front.stream(). map(
                    c -> c.calculateFitnessComponents(problem)).toArray(ChromoImSeg.Fitness[]::new);

            // save objectives and PRIs in .txt or .csv
            List<String> infoLines = new LinkedList<>();
            int optI= 0;
            double optPRI = 0.0;
            for (int i = 0; i < front.size(); i++) {
                StringBuilder chromoInfo = new StringBuilder();

                // write ID to String
                chromoInfo.append("Chromo ");
                chromoInfo.append(i);

                // write PRI to String
                double pri = PRIs[i];
                chromoInfo.append(". PRI: ");
                chromoInfo.append(pri);

                // find index of optimal PRI
                if (optPRI < pri) {
                    optPRI = pri;
                    optI = i;
                }

                // write fitness to String
                ChromoImSeg.Fitness fitness = objectives[i];
                chromoInfo.append(". Fitness, OD: ");
                chromoInfo.append(fitness.getDeviation());
                chromoInfo.append(", EV: ");
                chromoInfo.append(fitness.getEdge());
                chromoInfo.append(", C: ");
                chromoInfo.append(fitness.getConnectivity());

                // write number of segments to String
                chromoInfo.append(". Segments: ");
                chromoInfo.append(phenotypes.get(i).size());

                // conclude chromo
                chromoInfo.append(".");

                infoLines.add(chromoInfo.toString());
            }

            // prepend info for optimal chromosome
            infoLines.add(0, infoLines.get(optI));
            infoLines.add(1, "");

            Files.write(new File(directory + "info.txt").toPath(), infoLines, StandardOpenOption.CREATE);

            return new PRIOptimum(optPRI, front.get(optI));
        }

        private static void saveInColorGreen(String directory, collections.Image image, int width, int height, List<List<Segment>> phenotypes) throws IOException {
            ensurePathExists(directory + PATH_COLOR_GREEN);
            int[] coloredRawImage = image.rawImage();
            writePhenosToPngInFolderWithColor(phenotypes, coloredRawImage, directory + PATH_COLOR_GREEN,
                    width, height, GREEN);
        }

        private static void saveInBlackWhite(String directory, int width, int height, List<List<Segment>> phenotypes) throws IOException {
            ensurePathExists(directory + PATH_BLACK_WHITE);
            final int[] whiteRawImage = new int[width * height];
            Arrays.fill(whiteRawImage, WHITE);
            writePhenosToPngInFolderWithColor(phenotypes, whiteRawImage, directory + PATH_BLACK_WHITE,
                    width, height, BLACK);
        }

        public static void writePhenosToPngInFolderWithColor(final List<List<Segment>> phenotypes, int[] rawImage, String directory,
                                                             int width, int height, int color) throws IOException {
            int i = 0;
            for (List<Segment> pheno: phenotypes) {
                int[] tracedImage = ImageUtil.traceSegments(rawImage, pheno, color);
                ImageUtil.writeToFile(tracedImage, width, height, directory, "sol_" + i + ".png");
                i++;
            }
        }
    }

    public static final class PRIOptimum {

        private final double PRI;
        private final ChromoImSeg chromo;

        public PRIOptimum(double PRI, ChromoImSeg chromo) {
            this.PRI = PRI;
            this.chromo = chromo;
        }

        public double getPRI() {
            return PRI;
        }

        public ChromoImSeg getChromo() {
            return chromo;
        }
    }
}

