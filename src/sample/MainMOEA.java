package sample;

import collections.Image;
import evaluator.Evaluator;
import ga.GeneticAlgorithmRunner;
import ga.GeneticAlgorithmSnapshot;
import ga.nsga2.ParentSelectorMOEA;
import ga.nsga2.SurvivorSelectorMOEA;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import moea.ChromoImSeg;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.ga.*;

import java.beans.EventHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainMOEA extends Application {

    private static final int
            WHITE           = 0xffffffff,
            BLACK           = 0x00000000,
            IMAGE_WIDTH     = 241,
            IMAGE_HEIGHT    = 161,
            NUM_PREVIEW_IMAGES = 5,
            SCALING_FACTOR  = 2,
            HORIZONTAL_PREVIEWS = 3,
            VERTICAL_PREVIEWS = 2,
            SCREEN_WIDTH    = IMAGE_WIDTH * SCALING_FACTOR * HORIZONTAL_PREVIEWS,
            SCREEN_HEIGHT   = IMAGE_HEIGHT * SCALING_FACTOR * VERTICAL_PREVIEWS;

    private static final int IMAGE_CODE = 118035;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Evaluator evaluator = new Evaluator("./res/training_images/" + IMAGE_CODE + "/blackWhite/",
                "./sol/" + IMAGE_CODE + "/blackWhite/"  );

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

        primaryStage.setTitle("MOEA Image Segmentation");

        ImageView[] paretoOptimalPreviews = new ImageView[NUM_PREVIEW_IMAGES];
        WritableImage[] paretoOptimalImgs = new WritableImage[NUM_PREVIEW_IMAGES];
        for (int i = 0; i < NUM_PREVIEW_IMAGES; i++) {
            paretoOptimalImgs[i] = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            paretoOptimalPreviews[i] = new ImageView(paretoOptimalImgs[i]);

            paretoOptimalPreviews[i].setFitWidth(IMAGE_WIDTH * SCALING_FACTOR);
            paretoOptimalPreviews[i].setFitHeight(IMAGE_HEIGHT * SCALING_FACTOR);
            paretoOptimalPreviews[i].setX(IMAGE_WIDTH * SCALING_FACTOR * (i % HORIZONTAL_PREVIEWS));
            paretoOptimalPreviews[i].setY(IMAGE_HEIGHT * SCALING_FACTOR * (i / HORIZONTAL_PREVIEWS));
            System.out.println(IMAGE_WIDTH * SCALING_FACTOR * (i % 3) + ", " + IMAGE_HEIGHT * SCALING_FACTOR * (i / 3));
        }

        Text textPRI = new Text(IMAGE_WIDTH * SCALING_FACTOR * 2, IMAGE_HEIGHT * SCALING_FACTOR , "Put some PRI in me PLS!");
        Group dataView = new Group(textPRI);
//        dataView.setTranslateX();
        Group root = new Group(paretoOptimalPreviews);
        root.getChildren().add(dataView);

        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        gaRunner.valueProperty().addListener((obs, oldSnap, newSnap) -> updateOptimaPreviews(paretoOptimalPreviews,
                paretoOptimalImgs, problem, trainingImageRaw, newSnap.optima));

        Button button = makeButton(problem, gaRunner);
        root.getChildren().add(button);

        gaRunner.valueProperty().addListener( (obs, oldSnap, newSnap) -> {
            List<ChromoImSeg> optima = newSnap.optima;
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
        });

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

    public static void saveParetoFrontPreview(ProblemImSeg problem, List<ChromoImSeg> front) throws IOException {
        final Image image = problem.getImage();
        final int[][] buffers = new int[front.size()][problem.getPixelCount()];
        for (int i = 0; i < front.size(); i++) {
            Arrays.fill(buffers[i], WHITE);
            buffers[i] = ImageUtil.traceSegments(buffers[i], front.get(i).getPhenotype(problem), BLACK);
        }
        ImageUtil.writeImagesToFiles("./sol/" + IMAGE_CODE + "/blackWhite/", buffers, image.getWidth(), image.getHeight());
    }

    private Button makeButton(ProblemImSeg problem,
                              GeneticAlgorithmRunner<ProblemImSeg, PopulationImSeg, ChromoImSeg> gaRunner) {
        Button button = new Button("Evaluate");
        button.setOnAction(actionEvent -> {
            GeneticAlgorithmSnapshot<ChromoImSeg> snapshot = gaRunner.valueProperty().get();
            List<ChromoImSeg> firstFront = snapshot.optima;
            try {
                saveParetoFrontPreview(problem, firstFront);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Evaluator evaluator = new Evaluator("./res/training_images/" + IMAGE_CODE + "/blackWhite/",
                    "./sol/" + IMAGE_CODE + "/blackWhite/" );
            evaluator.valueProperty().addListener(((observableValue, previous, current) -> {
                if (current == null)
                    return;
                for (int i = 0; i < current.length; i++) {
                    System.out.println("Segementation " + i + ": " + current[i]);
                }
            }));
            Thread thread = new Thread(evaluator);
            thread.setDaemon(true);
            thread.start();
        });
        button.setLayoutY(SCREEN_HEIGHT / 2);
        button.setLayoutX(SCREEN_WIDTH / 3 * 2);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
