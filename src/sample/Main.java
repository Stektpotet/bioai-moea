package sample;

import collections.Image;
import collections.Segment;
import ga.RandomUtil;
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
import moea.ChromoImSeg;
import moea.ImSegFiles;
import moea.ProblemImSeg;
import moea.ga.*;
import moea.visual.ImSegVisualizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main extends Application {

    private static final int
            SCREEN_WIDTH = 1280,
            SCREEN_HEIGHT = (int)(1280/1.49689440994); // Aspect Ratio of image

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MOEA Image Segmentation");
//        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        ImageView imgView = new ImageView();

        ImageView groundTruthImageView = new ImageView(
                new javafx.scene.image.Image(new FileInputStream("./res/training_images/86016/Test image.jpg")));
        Group root = new Group(imgView, groundTruthImageView);
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

//        var visualizer = new ImSegVisualizer(SCREEN_WIDTH, SCREEN_HEIGHT, canvas.getGraphicsContext2D());

        ProblemImSeg problem = ImSegFiles.ReadImSegProblem("./res/training_images/86016/Test image.jpg");
        var breeder = new Breeder(problem, 4, 50);
        PopulationImSeg pop = breeder.breed(50);

        TournamentSelection parentSelector = new TournamentSelection(10, 4);
        UniformCrossoverer crossoverer = new UniformCrossoverer(0.5f);
        MutatorImSeg mutator = new MutatorImSeg(0.01f);
        MyPlusLambdaReplacement survivorSelector = new MyPlusLambdaReplacement(problem);

        for (int i = 0; i < 50; i++) {
            System.out.println("Doing generation: " + i);
            var parents = parentSelector.select(pop);
            var offspring = mutator.mutateAll(crossoverer.recombine(parents));
            pop = survivorSelector.select(pop, parents, offspring);
        }
        ChromoImSeg c = pop.getOptimum();

        Random colorRand = new Random(69);
//        ChromoImSeg c = pop.get(2);
        List<Segment> segments = c.getPhenotype(problem);
        int[] segmentColors = IntStream.generate(colorRand::nextInt).limit(segments.size()).toArray();

        Image problemImage = problem.getImage();
        WritableImage segmentImg = new WritableImage(problemImage.getWidth(), problemImage.getHeight());

        int[] segmentImgRaw = new int[problemImage.getPixelCount()];

        System.out.println(segments.size());
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            for (var p : segment.getEdge()) {
                segmentImgRaw[p] = 0x0000ff00;
            }
            for (var p : segment.getNonEdge()) { // for all pixels in a segment
                if (p >= problemImage.getPixelCount())
                    continue; // TODO: Does this ever happen?
                segmentImgRaw[p] = segmentColors[i];
            }
        }

        segmentImg.getPixelWriter().setPixels(
                0, 0, problemImage.getWidth(), problemImage.getHeight(),
                PixelFormat.getIntArgbPreInstance(), segmentImgRaw, 0, problemImage.getWidth()
        );

//        WritableImage img = new WritableImage(problemImage.getWidth(), problemImage.getHeight());
//        img.getPixelWriter().setPixels(
//                0, 0, problemImage.getWidth(), problemImage.getHeight(),
//                PixelFormat.getByteRgbInstance(), problemImage.getRawImage(), 0, problemImage.getWidth()*3);
        imgView.setImage(segmentImg);

//        imgView.setOpacity(0.1);
        imgView.setFitWidth(SCREEN_WIDTH * .5);
        imgView.setFitHeight(SCREEN_HEIGHT * .5);
        groundTruthImageView.setFitWidth(SCREEN_WIDTH * .5);
        groundTruthImageView.setFitHeight(SCREEN_HEIGHT * .5);
        groundTruthImageView.setX(SCREEN_WIDTH * .5);
//        imgView.resize(2, 2);
//        visualizer.drawGraph(problem);

        primaryStage.show();
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
