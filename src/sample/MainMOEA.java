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

import java.io.IOException;
import java.util.*;

public class MainMOEA extends Application {

    private static final int
            WHITE           = 0xffffffff,
            BLACK           = 0x00000000,
            IMAGE_WIDTH     = 241,
            IMAGE_HEIGHT    = 161,
            INFO_WIDTH      = 192,
            NUM_PREVIEW_IMAGES = 5,
            IMAGE_SCALING_FACTOR = 1,
            PANEL_HEIGHT    = (IMAGE_HEIGHT * IMAGE_SCALING_FACTOR) * 2,
            PANEL_WIDTH     = IMAGE_WIDTH * IMAGE_SCALING_FACTOR + INFO_WIDTH,
            HORIZONTAL_PREVIEWS = 3,
            VERTICAL_PREVIEWS = 2,
            SCREEN_WIDTH    = PANEL_WIDTH * HORIZONTAL_PREVIEWS,
            SCREEN_HEIGHT   = PANEL_HEIGHT * VERTICAL_PREVIEWS;

    private static final String
            LABEL_DEVIATION     = "Deviation:    ",
            LABEL_EDGE          = "Edge value:  ",
            LABEL_CONNECTIVITY  = "Connectivity: ";

    private static final int[] TEST_IMAGE_CODES = new int[] {86016, 118035, 147091, 176035, 176039, 353013};
    private static final int IMAGE_CODE = TEST_IMAGE_CODES[2];

    @Override
    public void start(Stage primaryStage) throws Exception {

        var start = System.nanoTime();
        ProblemImSeg problem = ImSegFiles.ReadImSegProblem("./res/training_images/" + IMAGE_CODE + "/Test image.jpg");
        System.out.println("Problem reading took: " + (System.nanoTime() - start)/1000000 + "ms");
        GeneticAlgorithmRunner<ProblemImSeg, PopulationImSeg, ChromoImSeg> gaRunner = new GeneticAlgorithmRunner<>(
                new Breeder(problem, 5, 25),
                new SegmentationCrossover(0.7f, 2, problem),
                new MutatorImSeg(0.3f),
                new ParentSelectorMOEA(10, 50),
                new SurvivorSelectorMOEA(),
                200
        );

        ImageView[] paretoOptimalPreviews = new ImageView[NUM_PREVIEW_IMAGES * 2];
        WritableImage[] paretoOptimalImgs = new WritableImage[NUM_PREVIEW_IMAGES * 2];
        Text[] paretoOptimalStats = new Text[NUM_PREVIEW_IMAGES * 3];
        guiComponentSetup(paretoOptimalPreviews, paretoOptimalImgs, paretoOptimalStats);

        Group dataView = new Group(paretoOptimalStats);
        Group previewView = new Group(paretoOptimalPreviews);
        Group root = new Group(previewView, dataView);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);

        primaryStage.setTitle("MOEA Image Segmentation");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        int[] trainingImageRaw = problem.getImage().rawImage();
        gaRunner.valueProperty().addListener((obs, oldSnap, newSnap) -> updateOptimaPreviews(
                paretoOptimalImgs, paretoOptimalStats, problem, trainingImageRaw, newSnap.optima));

        Button button = makeButton(problem, gaRunner);
        root.getChildren().add(button);


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

    private void guiComponentSetup(ImageView[] previews, WritableImage[] previewImages, Text[] stats) {
        for (int i = 0; i < NUM_PREVIEW_IMAGES * 2; i++) {  // Create the images and set sizes
            previewImages[i] = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            previews[i] = new ImageView(previewImages[i]);
            previews[i].setFitWidth(IMAGE_WIDTH * IMAGE_SCALING_FACTOR);
            previews[i].setFitHeight(IMAGE_HEIGHT * IMAGE_SCALING_FACTOR);
            ImageUtil.fillImage(previewImages[i], 0x00111111);
        }
        for (int i = 0; i < NUM_PREVIEW_IMAGES; i++) {
            double x = PANEL_WIDTH * (i % HORIZONTAL_PREVIEWS);
            double y = PANEL_HEIGHT * (i / HORIZONTAL_PREVIEWS);
            previews[i * 2].setX(x);
            previews[i * 2 + 1].setX(x);
            previews[i * 2].setY(y);
            previews[i * 2 + 1].setY(y + IMAGE_HEIGHT);

            x += IMAGE_WIDTH;
            stats[i * 3    ] = new Text(x + 16, y + 16, LABEL_DEVIATION);
            stats[i * 3 + 1] = new Text(x + 16, y + 32, LABEL_EDGE);
            stats[i * 3 + 2] = new Text(x + 16, y + 48, LABEL_CONNECTIVITY);
        }
        for (Text paretoOptimalObjective : stats) {
            paretoOptimalObjective.setFill(Color.GREEN);
            paretoOptimalObjective.setFont(Font.font("Consolas", 12));
        }
    }

    private void updateOptimaPreviews(WritableImage[] paretoOptimalImgs, Text[] paretoOptimalObjectives,
                                      ProblemImSeg problem, int[] trainingImageRaw, List<ChromoImSeg> optima) {
        ChromoImSeg[] optimaUnique = optima.stream().distinct().limit(NUM_PREVIEW_IMAGES).toArray(ChromoImSeg[]::new);
        for (int i = 0; i < paretoOptimalImgs.length; i++) {
            if (i >= optimaUnique.length) {
                if (i < NUM_PREVIEW_IMAGES) {
                    ImageUtil.clearImage(paretoOptimalImgs[i * 2]);
                    ImageUtil.clearImage(paretoOptimalImgs[i * 2 + 1]);
                }
                continue;
            }
            var phenotype = optimaUnique[i].getPhenotype(problem);
            int[] traced = ImageUtil.traceSegments(trainingImageRaw, phenotype);
            ImageUtil.writeImage(paretoOptimalImgs[i * 2], traced);

            int[] binaryImage = new int[trainingImageRaw.length];
            Arrays.fill(binaryImage, 0xffffffff);
            binaryImage = ImageUtil.traceSegments(binaryImage, phenotype, 0xff000000);
            ImageUtil.writeImage(paretoOptimalImgs[i * 2 + 1], binaryImage);

            ChromoImSeg.Fitness fitness = optimaUnique[i].calculateFitnessComponents(problem);
            paretoOptimalObjectives[i * 3].setText(String.format("%s%.2f", LABEL_DEVIATION, fitness.getDeviation()));
            paretoOptimalObjectives[i * 3 + 1].setText(String.format("%s%.2f", LABEL_EDGE, fitness.getEdge()));
            paretoOptimalObjectives[i * 3 + 2].setText(String.format("%s%.2f", LABEL_CONNECTIVITY, fitness.getConnectivity()));
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
                    if (current[i] > 0.7)
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
