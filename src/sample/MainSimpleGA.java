package sample;

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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import moea.ChromoImSeg;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.ga.*;

import java.util.Arrays;
import java.util.List;

public class MainSimpleGA extends Application {

    private static final int
            IMAGE_WIDTH     = 241,
            IMAGE_HEIGHT    = 161,
            INFO_WIDTH      = 256,
            IMAGE_SCALING_FACTOR = 2,
            PANEL_HEIGHT    = IMAGE_HEIGHT * IMAGE_SCALING_FACTOR,
            PANEL_WIDTH     = (IMAGE_WIDTH * IMAGE_SCALING_FACTOR) + INFO_WIDTH,
            HORIZONTAL_PREVIEWS = 1,
            VERTICAL_PREVIEWS = 2,
            SCREEN_WIDTH    = PANEL_WIDTH * HORIZONTAL_PREVIEWS,
            SCREEN_HEIGHT   = PANEL_HEIGHT * VERTICAL_PREVIEWS;

    private static final String
            LABEL_DEVIATION     = "Deviation:    ",
            LABEL_EDGE          = "Edge value:  ",
            LABEL_CONNECTIVITY  = "Connectivity: ",
            LABEL_FITNESS       = "Fitness:      ",
            LABEL_GENERATION    = "Generation: ";
    private static final String[] LABELS = new String[] {LABEL_DEVIATION, LABEL_EDGE, LABEL_DEVIATION, LABEL_FITNESS};

    private static final int[] TEST_IMAGE_CODES = new int[] {86016, 118035, 147091, 176035, 176039, 353013};
    private static final int IMAGE_CODE = TEST_IMAGE_CODES[0];
    private static final String PATH_TO_SOLUTION_FOLDER = "./sol/simple/" + IMAGE_CODE + "/";
    private static final String PATH_TO_GT_FOLDER = "./res/training_images/" + IMAGE_CODE + "/blackWhite/";
    private static final String PATH_TO_PROBLEM = "./res/training_images/" + IMAGE_CODE + "/Test image.jpg";


    @Override
    public void start(Stage primaryStage) throws Exception {

        long start = System.nanoTime();
        ProblemImSeg problem = ImSegFiles.ReadImSegProblem(PATH_TO_PROBLEM);
        System.out.println("Problem reading took: " + (System.nanoTime() - start)/1000000 + "ms");
        GeneticAlgorithmRunner<ProblemImSeg, PopulationImSeg, ChromoImSeg> gaRunner = new GeneticAlgorithmRunner<>(
                new Breeder(problem, 5, 25),
                new UniformCrossoverer(0.7f),
                new MutatorImSeg(0.3f),
                new TournamentSelection(10, 10),
                new MyPlusLambdaReplacement(problem),
                10
        );

        ImageView[] previewViews = new ImageView[2];
        WritableImage[] previewImages = new WritableImage[2];
        Text[] stats = new Text[4];
        guiComponentSetup(previewViews, previewImages, stats);

        Text generationCounter = new Text(
                IMAGE_WIDTH * IMAGE_SCALING_FACTOR,
                IMAGE_HEIGHT * IMAGE_SCALING_FACTOR - 32,
                String.format("%s#%05d", LABEL_GENERATION, 0)
        );
        generationCounter.setFill(Color.WHITE);
        generationCounter.setFont(Font.font("Consolas", 20));
        Group root = new Group(new Group(stats), new Group(previewViews), generationCounter);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);

        primaryStage.setTitle("SimpleGA Image Segmentation");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        gaRunner.valueProperty().addListener((obs, oldSnap, newSnap) -> {
            updateOptimaPreviews(previewImages, stats, problem, newSnap.optima);
            generationCounter.setText(String.format("%s#%05d", LABEL_GENERATION, newSnap.currentGeneration));
        });

        Button button = makeButton(problem, gaRunner);
        root.getChildren().add(button);

        primaryStage.show();

        gaRunner.start();
    }

    private void guiComponentSetup(ImageView[] previews, WritableImage[] previewImages, Text[] stats) {
        for (int i = 0; i < 2; i++) {
            previewImages[i] = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            previews[i] = new ImageView(previewImages[i]);

            previews[i].setFitWidth(IMAGE_WIDTH * IMAGE_SCALING_FACTOR);
            previews[i].setFitHeight(IMAGE_HEIGHT * IMAGE_SCALING_FACTOR);

            double x = PANEL_WIDTH * (i % HORIZONTAL_PREVIEWS);
            double y = PANEL_HEIGHT * (i / HORIZONTAL_PREVIEWS);
            previews[i].setX(x);
            previews[i].setY(y);

        }
        for (int i = 0; i < stats.length; i++) {
            stats[i] = new Text((IMAGE_WIDTH * IMAGE_SCALING_FACTOR) + 32, 32 + 16 * i, LABELS[i]);
            stats[i].setFill(Color.GREEN);
            stats[i].setFont(Font.font("Consolas", 12));
        }
    }

    private void updateOptimaPreviews(WritableImage[] previewImages, Text[] stats,
                                      ProblemImSeg problem, List<ChromoImSeg> optima) {
        ChromoImSeg best = optima.get(0);
        var trainingImageRaw = problem.getImage().rawImage();
        int[] traced = ImageUtil.traceSegments(trainingImageRaw, best.getPhenotype(problem));
        ImageUtil.writeImage(previewImages[0], traced);


        int[] binaryImage = new int[trainingImageRaw.length];
        Arrays.fill(binaryImage, 0xffffffff);
        binaryImage = ImageUtil.traceSegments(binaryImage, best.getPhenotype(problem), 0xff000000);
        ImageUtil.writeImage(previewImages[1], binaryImage);

        ChromoImSeg.Fitness fitness = best.calculateFitnessComponents(problem);
        stats[0].setText(String.format("%s%.2f", LABEL_DEVIATION, fitness.getDeviation()));
        stats[1].setText(String.format("%s%.2f", LABEL_EDGE, fitness.getEdge()));
        stats[2].setText(String.format("%s%.2f", LABEL_CONNECTIVITY, fitness.getConnectivity()));
        stats[3].setText(String.format("%s%.2f", LABEL_FITNESS, best.fitness(problem)));
    }

    private Button makeButton(ProblemImSeg problem,
                              GeneticAlgorithmRunner<ProblemImSeg, PopulationImSeg, ChromoImSeg> gaRunner) {
        Button button = new Button("Evaluate");
        button.setOnAction(actionEvent -> {
            GeneticAlgorithmSnapshot<ChromoImSeg> snapshot = gaRunner.valueProperty().get();
            List<ChromoImSeg> firstFront = snapshot.optima.subList(0, 1);

            ImageUtil.deleteRecursively(PATH_TO_SOLUTION_FOLDER);

            ImageUtil.Output output = new ImageUtil.Output(firstFront, problem,
                    PATH_TO_SOLUTION_FOLDER, PATH_TO_GT_FOLDER);
            output.valueProperty().addListener((observableValue, previous, current) -> {
                // TODO implement listener
                System.out.println("listened");
            });

            Thread thread = new Thread(output);
            thread.setDaemon(true);
            thread.start();
        });
        button.setLayoutY(IMAGE_HEIGHT * IMAGE_SCALING_FACTOR);
        button.setLayoutX(IMAGE_WIDTH * IMAGE_SCALING_FACTOR);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
