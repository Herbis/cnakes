package lv.herbis.cnakes.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores implements Serializable {

    public static final int MAX_LIMIT = Integer.MAX_VALUE;
    private static final long serialVersionUID = 5871033699197551246L;

    private int LIMIT;

    List<HighScore> highScores;

    /**
     * Creates unlimited list of High Scores.
     */
    public HighScores() {
        highScores = new ArrayList<>();
        changeLimit(MAX_LIMIT);
    }

    /**
     * Creates a list of High Scores with the given limit.
     *
     * @param limit How many High Scores to keep.
     */
    public HighScores(final int limit) {
        highScores = new ArrayList<>();
        changeLimit(limit);
    }


    /**
     * Adds given score to the high-score list only if it's larger than the last
     * score or the high-score list limit hasn't been reached.
     *
     * @param score User's score.
     * @return Returns true if user's score was added to the list, false otherwise.
     */
    public boolean addHighScore(final HighScore score) {
        final int highScoreSize = highScores.size();
        /* Check if highscore list has reached (or is over) the limit. */
        if (LIMIT <= highScoreSize) {
            if (getBottomScore().getScore() < score.getScore()) {
                /* Remove last element, add new score and sort. */
                highScores.remove(highScoreSize - 1);
                addHighScoreToList(score, true);

                //TODO method to remove all elements outside the limit
                return true;
            }
            return false; // Didn't add to high scores.
        }

        /*So we haven't reached the high-score count limit, we can just add to the list. */
        addHighScoreToList(score, true);

        return true;
    }


    /**
     * Adds high score directly to the list and sorts it, if needed.
     *
     * @param score HighScore object to add to the list.
     * @param sort  Should the list be sorted? (Most likely always true).
     */
    private void addHighScoreToList(final HighScore score, final boolean sort) {
        highScores.add(score);
        if (sort) {
            Collections.sort(highScores);
        }
    }


    /**
     * Change the limit of how many High Scores there can be.
     *
     * @param newLimit new limit.
     */
    public void changeLimit(final int newLimit) {
        if (MAX_LIMIT < newLimit || newLimit <= 0) {
            throw new IllegalArgumentException("HighScore limit invalid: " + newLimit);
        }
        LIMIT = newLimit;
    }


    /**
     * Returns list of High Scores.
     *
     * @return Returns a List of HighScore objects.
     */
    public List<HighScore> getHighScoreList() {
        return highScores;
    }


    /**
     * Returns the top high score.
     *
     * @return HighScore object that is the first (top) in the List of scores.
     */
    public HighScore getTopScore() {
        return highScores.size() > 0 ? highScores.get(0) : null;
    }


    /**
     * Returns the bottom high score.
     *
     * @return HighScore object that is the last (lowest) in the List of scores.
     */
    public HighScore getBottomScore() {
        final int highScoreSize = highScores.size();
        return highScoreSize > 0 ? highScores.get(highScoreSize - 1) : null;
    }
}
