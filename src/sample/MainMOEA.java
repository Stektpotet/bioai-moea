package sample;

import collections.Image;
import ga.GeneticAlgorithmRunner;
import ga.GeneticAlgorithmSnapshot;
import ga.nsga2.ParentSelectorMOEA;
import ga.nsga2.SurvivorSelectorMOEA;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import moea.ChromoImSeg;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.ga.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainMOEA extends Application {

    private static final int
            IMAGE_WIDTH     = 241,
            IMAGE_HEIGHT    = 161,
            NUM_PREVIEW_IMAGES = 5,
            SCREEN_WIDTH    = IMAGE_WIDTH * 2 * 3,
            SCREEN_HEIGHT   = IMAGE_HEIGHT * 2 * 2; //(int)(1280/1.49689440994); // Aspect Ratio of image

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MOEA Image Segmentation");

        ImageView[] paretoOptimalPreviews = new ImageView[NUM_PREVIEW_IMAGES];
        WritableImage[] paretoOptimalImgs = new WritableImage[NUM_PREVIEW_IMAGES];
        for (int i = 0; i < NUM_PREVIEW_IMAGES; i++) {
            paretoOptimalImgs[i] = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            paretoOptimalPreviews[i] = new ImageView(paretoOptimalImgs[i]);
            paretoOptimalPreviews[i].setFitWidth(IMAGE_WIDTH * 2);
            paretoOptimalPreviews[i].setFitHeight(IMAGE_HEIGHT * 2);
            paretoOptimalPreviews[i].setX(IMAGE_WIDTH * 2 * (i % 3));
            paretoOptimalPreviews[i].setY(IMAGE_HEIGHT * 2 * (i / 3));
        }

        Group root = new Group(paretoOptimalPreviews);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        var start = System.nanoTime();
        //TODO make main take the image code
        ProblemImSeg problem = ImSegFiles.ReadImSegProblem("./res/training_images/118035/Test image.jpg");
        System.out.println("Problem reading took: " + (System.nanoTime() - start)/1000000 + "ms");
        GeneticAlgorithmRunner<ProblemImSeg, PopulationImSeg, ChromoImSeg> gaRunner = new GeneticAlgorithmRunner<>(
                new Breeder(problem, 1, 15),
                new UniformCrossoverer(0.5f),
                new MutatorImSeg(0.7f),
                new ParentSelectorMOEA(10, 2),
                new SurvivorSelectorMOEA(),
                50
        );
        int[] trainingImageRaw = problem.getImage().rawImage();

        gaRunner.valueProperty().addListener((obs, oldSnap, newSnap) -> updateOptimaPreviews(paretoOptimalPreviews, paretoOptimalImgs, problem, trainingImageRaw, newSnap.optima));

        primaryStage.setOnCloseRequest(event -> {
            GeneticAlgorithmSnapshot<ChromoImSeg> stateSnapshot = gaRunner.valueProperty().get();
//          TODO: Save aside current optimum
//            try {
//                SaveParetoFrontPreview(problem, stateSnapshot.optima);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        });
        primaryStage.show();
        gaRunner.start();
    }

    private void updateOptimaPreviews(ImageView[] paretoOptimalPreviews, WritableImage[] paretoOptimalImgs, ProblemImSeg problem, int[] trainingImageRaw, List<ChromoImSeg> optima) {
        int segmentationsToShow = Math.min(optima.size(), 5);
        for (int i = 0; i < paretoOptimalImgs.length; i++) {
            if (i >= segmentationsToShow) {
                ImageUtil.clearImage(paretoOptimalImgs[i]);
                continue;
            }
            int[] traced = ImageUtil.traceSegments(trainingImageRaw, optima.get(i).getPhenotype(problem));
            ImageUtil.writeImage(paretoOptimalImgs[i], traced);
            paretoOptimalPreviews[i].setImage(paretoOptimalImgs[i]);
        }
    }

    public static void SaveParetoFrontPreview(ProblemImSeg problem, List<ChromoImSeg> front) throws IOException {
        final Image image = problem.getImage();
        final int[][] buffers = new int[problem.getPixelCount()][front.size()];
        for (int i = 0; i < front.size(); i++) {
            Arrays.fill(buffers[i], 0xffffffff);
            ImageUtil.traceSegments(buffers[i],  front.get(i).getPhenotype(problem), 0);
        }
        ImageUtil.writeImagesToFiles("./sol/118035/blackWhite/", buffers, image.getWidth(), image.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
