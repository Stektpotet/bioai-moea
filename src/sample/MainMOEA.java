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
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import moea.ChromoImSeg;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.ga.*;

import java.io.FileInputStream;
import java.io.IOException;
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

        // FeedbackStation feedbackStation = new FeedbackStation();
        // Evaluator evaluator = new Evaluator("./res/training_images/86016/blackWhite/",
           //     "./sol/86016/blackWhite/", feedbackStation);
        // new Thread(evaluator).start();


        primaryStage.setTitle("MOEA Image Segmentation");
//        var previewImg = new javafx.scene.image.Image(
//                new FileInputStream("./res/training_images/118035/Test image.jpg"),
//                241, 161, true, false
//        );
        ImageView[] paretoOptimalPreviews = new ImageView[NUM_PREVIEW_IMAGES];
        WritableImage[] paretoOptimalImgs = new WritableImage[NUM_PREVIEW_IMAGES];
        for (int i = 0; i < NUM_PREVIEW_IMAGES; i++) {
            paretoOptimalImgs[i] = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
            paretoOptimalPreviews[i] = new ImageView(paretoOptimalImgs[i]);
            paretoOptimalPreviews[i].setFitWidth(IMAGE_WIDTH * 2);
            paretoOptimalPreviews[i].setFitHeight(IMAGE_HEIGHT * 2);
            paretoOptimalPreviews[i].setX(IMAGE_WIDTH * 2 * (i % 3));
            paretoOptimalPreviews[i].setY(IMAGE_HEIGHT * 2 * (i / 3));
            System.out.println(IMAGE_WIDTH * 2 * (i % 3) + ", " + IMAGE_HEIGHT * 2 * (i / 3));
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
        Image image = problem.getImage();
        int[] trainingImageRaw = image.rawImage();

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
            List<ChromoImSeg> optima = stateSnapshot.optima;
            int segmentationsToShow = Math.min(optima.size(), 5);
            for (int i = 0; i < segmentationsToShow; i++) {
                var img = paretoOptimalImgs[i];
                ImageUtil.fillImage(img, 0xffffffff);
                ImageUtil.traceSegmentsOnto(img,  optima.get(i).getPhenotype(problem), 0);

            }
                try {
                    ImageUtil.writeFrontToFiles("./sol/118035/blackWhite/", paretoOptimalImgs);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//            long diff = (System.currentTimeMillis()-start);
//            System.out.println(String.format("Ended after %02d:%02d", (diff / (1000 * 60)) % 60, (diff / 1000) % 60));
//          TODO: Save aside current optimum

//            SAVE SCREENSHOT of the application running
//            WritableImage snapshot = root.snapshot(null, null);
//            Img.WriteImg(snapshot, String.format("solutions/%s.png", problem.getName()));

        });

//        imgView.setOpacity(0.1);
//        imgView.setFitWidth(SCREEN_WIDTH * .5);
//        imgView.setFitHeight(SCREEN_HEIGHT * .5);
//        trainingImageView.setFitWidth(SCREEN_WIDTH * .5);
//        trainingImageView.setFitHeight(SCREEN_HEIGHT * .5);
//        trainingImageView.setX(SCREEN_WIDTH * .5);
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
