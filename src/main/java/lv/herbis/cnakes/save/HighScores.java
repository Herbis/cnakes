package lv.herbis.cnakes.save;

import lv.herbis.cnakes.tools.DataUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores implements Serializable {

	public static final int MAX_LIMIT = 100_000;
	private static final long serialVersionUID = 5871033699197551246L;
	private static final Logger LOG = LogManager.getLogger(HighScores.class);

	private int limit;

	private List<HighScore> highScoreList;

	/**
	 * Creates unlimited list of High Scores.
	 */
	public HighScores() {
		this.highScoreList = new ArrayList<>();
		changeLimit(MAX_LIMIT);
	}

	/**
	 * Creates a list of High Scores with the given limit.
	 *
	 * @param limit How many High Scores to keep.
	 */
	public HighScores(final int limit) {
		this.highScoreList = new ArrayList<>();
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
		final int highScoreSize = getHighScoreList().size();
		/* Check if high-score list has reached (or is over) the limit. */
		if (getLimit() <= highScoreSize) {
			LOG.debug("High-score limit ({}) reached.", getLimit());
			final HighScore bottomScore = getBottomScore();
			if (bottomScore == null || bottomScore.getScore() < score.getScore()) {
				/* Add new score and sort. */
				addHighScoreToList(score, true);
				DataUtil.removeItemsAboveIndex(getHighScoreList(), getLimit() - 1);
				return true;
			} else {
				LOG.debug("Score lower than the bottom score.");
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
		getHighScoreList().add(score);
		if (sort) {
			Collections.sort(getHighScoreList());
		}
	}

	/**
	 * Returns the High-score entry limit. Should always return value above 0.
	 *
	 * @return High-score entry limit.
	 */
	public int getLimit() {
		if (this.limit <= 0) {
			final int newLimit = MAX_LIMIT;
			LOG.debug("High-Score limit was {}, automatically adjusted to {}", this.limit, newLimit);
			this.limit = newLimit;
		}
		return this.limit;
	}

	/**
	 * Change the limit of how many High Scores there can be.
	 *
	 * @param newLimit new limit.
	 */
	public void changeLimit(final int newLimit) {
		if (newLimit <= 0) {
			throw new IllegalArgumentException("HighScore limit invalid: " + newLimit);
		}
		this.limit = newLimit;
	}


	/**
	 * Returns list of High Scores.
	 *
	 * @return Returns a List of HighScore objects.
	 */
	public List<HighScore> getHighScoreList() {
		if (this.highScoreList == null) {
			this.highScoreList = new ArrayList<>();
		}

		return this.highScoreList;
	}

	/**
	 * Returns the top high score.
	 *
	 * @return HighScore object that is the first (top) in the List of scores.
	 */
	public HighScore getTopScore() {
		return getHighScoreList().isEmpty() ? null : getHighScoreList().get(0);
	}


	/**
	 * Returns the bottom high score.
	 *
	 * @return HighScore object that is the last (lowest) in the List of scores.
	 */
	public HighScore getBottomScore() {
		final int highScoreSize = getHighScoreList().size();
		return highScoreSize > 0 ? getHighScoreList().get(highScoreSize - 1) : null;
	}
}
