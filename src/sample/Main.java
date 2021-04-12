package sample;

import collections.Image;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.visual.ImSegVisualizer;

public class Main extends Application {

    private static final int
            SCREEN_WIDTH = 1280,
            SCREEN_HEIGHT = (int)(1280/1.49689440994); // Aspect Ratio of image

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MOEA Image Segmentation");
//        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        ImageView imgView = new ImageView();
        Group root = new Group(imgView);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

//        var visualizer = new ImSegVisualizer(SCREEN_WIDTH, SCREEN_HEIGHT, canvas.getGraphicsContext2D());

        ProblemImSeg problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");
        Image problemImage = problem.getImage();
        WritableImage img = new WritableImage(problemImage.getWidth(), problemImage.getHeight());
        img.getPixelWriter().setPixels(
                0, 0, problemImage.getWidth(), problemImage.getHeight(),
                PixelFormat.getByteRgbInstance(), problemImage.getRawImage(), 0, problemImage.getWidth()*3);
        imgView.setImage(img);
        imgView.setFitWidth(SCREEN_WIDTH * .5);
        imgView.setFitHeight(SCREEN_HEIGHT * .5);
//        imgView.resize(2, 2);
//        visualizer.drawGraph(problem);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
