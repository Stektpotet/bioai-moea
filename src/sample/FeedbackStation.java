package sample;

public class FeedbackStation {

    double[] scores;
    boolean calculated = false;

    public boolean isCalculated() {
        return calculated;
    }

    public double[] getScores() {
        return scores;
    }

    public void registerScores(final double[] scores) {
        this.scores = scores;
        calculated = true;
    }
}
