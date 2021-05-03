package evaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

/**
 * How to use:
 * Requires JavaFX.
 * After having placed the ground truth images and your segmentations in their appropriate folders, call runSameThread().
 * If using from a GUI or other multithreaded contexts, i recommend uncommenting the lines referencing
 * the "feedbackStation" object, wrapping this object in a Thread object, then calling start() on that thread.
 * You will need to implement the FeedbackStation interface yourselves.
 */
public final class Evaluator {
    String optFolder;
    String studFolder;

    final double colorValueSlackRange = 40.0/255.0;
    final double blackValueThreshold = 100.0/255.0;
    final int pixelCheckRange = 4;
    final boolean checkEightSurroundingPixels = true;

    List<File> optFiles;
    List<File> studFiles;
    List<Image> optImages;
    List<Image> studImages;


    public Evaluator(String optFolder, String studFolder){
        optFiles = new ArrayList<>();
        studFiles = new ArrayList<>();
        optImages = new ArrayList<>();
        studImages = new ArrayList<>();
        this.optFolder = optFolder;
        this.studFolder = studFolder;
    }

    public double[] runSameThread() throws FileNotFoundException {
        updateOptimalFiles();
        updateStudentFiles();
        updateImageLists();
        double[] scores = evaluate();
        return scores;
    }

    public double[] evaluate(){
        double[] scores = new double[studImages.size()];
        for(int i=0; i<studImages.size(); i++){
            Image studImg = studImages.get(i);
            double highestScore = 0.0;
            for(Image optImg : optImages){
                double res1 = compare(studImg, optImg);
                double res2 = compare(optImg, studImg);
                double result = Math.min(res1, res2);
                highestScore = Math.max(highestScore, result);
            }
            scores[i] = highestScore;
        }
        return scores;
    }


    private double compare(Image optImg, Image studImg){
        PixelReader opt = optImg.getPixelReader();
        PixelReader stud = studImg.getPixelReader();
        while(opt == null || stud == null){
            opt = optImg.getPixelReader();
            stud = studImg.getPixelReader();
        }

        int numBlackPixels = 0;
        int counter = 0;  // number of similar pixels.

        for(int w=0; w<optImg.getWidth(); w+=1){
            for(int h=0; h<optImg.getHeight(); h++){
                double cOpt = opt.getColor(w, h).getBrightness();
                double cStud = stud.getColor(w, h).getBrightness();

                if(cStud < blackValueThreshold){
                    numBlackPixels++;
                    if(cOpt < blackValueThreshold){
                        counter++;
                    }
                    else if(checkEightSurroundingPixels){
                        boolean correctFound = false;
                        for(int w2=w-pixelCheckRange; w2<=w+pixelCheckRange; w2++){
                            if(correctFound)
                                break;
                            if(w2 < 0 || w2 >= optImg.getWidth())
                                continue;

                            for(int h2=h-pixelCheckRange; h2<=h+pixelCheckRange; h2++){
                                if(h2 < 0 || h2 >= optImg.getHeight())
                                    continue;

                                cOpt = opt.getColor(w2, h2).getBrightness();
                                if(cStud-colorValueSlackRange < cOpt && cOpt < cStud+colorValueSlackRange){
                                    correctFound = true;
                                    counter++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return counter / Math.max(numBlackPixels, 1.0);
    }

    private List<File> getFilesInDir(String directory){
        File dir = new File(directory);
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(dir.listFiles()));
        File[] ordered = new File[files.size()];
        for(File f : files){
            String lastPart = f.getName().split("_")[1];
            int num = Integer.parseInt(lastPart.substring(0, lastPart.length()-4));
            ordered[num] = f;
        }
        files = Arrays.asList(ordered);
        return files;
    }

    public void updateOptimalFiles(){
        optFiles = getFilesInDir(optFolder);
    }

    public void updateStudentFiles(){
        studFiles = getFilesInDir(studFolder);
    }

    private void updateImageLists() throws FileNotFoundException {
        optImages.clear();
        for(File f : optFiles){
            var img = new Image(
                    new FileInputStream(f.getAbsolutePath())
            );
            optImages.add(img); // true is for background loading
        }
        studImages.clear();
        for(File f : studFiles){
            var img = new Image(
                    new FileInputStream(f.getAbsolutePath())
            );
            studImages.add(img); // true is for background loading
        }
    }
}
