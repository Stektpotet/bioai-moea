package sample;

import collections.Image;
import collections.Segment;
import ga.GeneticAlgorithmRunner;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import moea.ChromoImSeg;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.ga.*;

import java.io.FileInputStream;
import java.util.List;

public class MainSimpleGA extends Application {

    private static final int
            SCREEN_WIDTH = 1280,
            SCREEN_HEIGHT = (int)(1280/1.49689440994); // Aspect Ratio of image

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MOEA Image Segmentation");
        var previewImg = new javafx.scene.image.Image(
                new FileInputStream("./res/training_images/147091/Test image.jpg"),
                241, 161, true, false
        );
        ImageView imgView = new ImageView(previewImg);
        ImageView groundTruthImageView = new ImageView(previewImg);
        Group root = new Group(imgView, groundTruthImageView);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        var start = System.nanoTime();
        ProblemImSeg problem = ImSegFiles.ReadImSegProblem("./res/training_images/147091/Test image.jpg");
        System.out.println("Problem reading took: " + (System.nanoTime() - start)/1000000 + "ms");
        GeneticAlgorithmRunner<ProblemImSeg, PopulationImSeg, ChromoImSeg> gaRunner = new GeneticAlgorithmRunner<>(
                new Breeder(problem, 10, 50),
                new UniformCrossoverer(1.0f),
                new MutatorImSeg(1.0f),
                new TournamentSelection(2, 4),
                new MyPlusLambdaReplacement(problem),
                60
        );

        Image image = problem.getImage();
        int[] groundTruthImageRaw = new int[image.getPixelCount()];
        groundTruthImageView.getImage().getPixelReader().getPixels(
                0, 0, image.getWidth(), image.getHeight(),
                PixelFormat.getIntArgbPreInstance(), groundTruthImageRaw,
                0, image.getWidth()
        );

        gaRunner.valueProperty().addListener( (obs, oldSnap, newSnap) -> {
            List<Segment> segments = newSnap.optima.get(0).getPhenotype(problem);
            WritableImage segmentImg = new WritableImage(image.getWidth(), image.getHeight());

            int[] segmentImgRaw = groundTruthImageRaw.clone();

            for (Segment segment : segments) {
                for (var p : segment.getEdge()) {
                    segmentImgRaw[p] = 0x0000ff00; // green
                }
            }

            segmentImg.getPixelWriter().setPixels(
                    0, 0, image.getWidth(), image.getHeight(),
                    PixelFormat.getIntArgbPreInstance(), segmentImgRaw, 0, image.getWidth()
            );
            imgView.setImage(segmentImg);
        });

//        imgView.setOpacity(0.1);
        imgView.setFitWidth(SCREEN_WIDTH * .5);
        imgView.setFitHeight(SCREEN_HEIGHT * .5);
        groundTruthImageView.setFitWidth(SCREEN_WIDTH * .5);
        groundTruthImageView.setFitHeight(SCREEN_HEIGHT * .5);
        groundTruthImageView.setX(SCREEN_WIDTH * .5);
        primaryStage.show();

        gaRunner.start();
    }

    public static int getRGB(Color col) {
        int r = ((int) Color.RED.getRed() * 255);
        int g = ((int) Color.RED.getGreen() * 255);
        int b = ((int) Color.RED.getBlue() * 255);
        return (r << 16) + (g << 8) + b;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
