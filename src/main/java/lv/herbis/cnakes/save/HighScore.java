package lv.herbis.cnakes.save;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * High-Score object, representing user's High-Score.
 *
 * @author Herberts Markuns
 */
public class HighScore implements Serializable, Comparable<HighScore> {

	private static final long serialVersionUID = -7990884209338408457L;

	private long score;
	private long timestamp;
	private String username;


	/**
	 * Create an HighScore object, with given username and score. Timestamp will be given
	 * from creation time.
	 *
	 * @param username User's username.
	 * @param score    User's score.
	 */
	public HighScore(final String username, final long score) {
		setUsername(username);
		setScore(score);
		setTimestamp(Calendar.getInstance().getTimeInMillis());
	}


	/**
	 * Create an HighScore object, with given username, score and timestamp.
	 *
	 * @param username  User's username.
	 * @param score     User's score.
	 * @param timestamp Timestamp (date represented in milliseconds).
	 */
	public HighScore(final String username, final long score, final long timestamp) {
		setUsername(username);
		setScore(score);
		setTimestamp(timestamp);
	}


	/**
	 * Returns score.
	 *
	 * @return Returns score.
	 */
	public long getScore() {
		return this.score;
	}


	/**
	 * Returns the timestamp (date represented in milliseconds) for this High-Score.
	 *
	 * @return Returns timestamp (date represented in milliseconds).
	 */
	public long getTimestamp() {
		return this.timestamp;
	}


	/**
	 * Returns the user's username;
	 *
	 * @return String containing user's username;
	 */
	public String getUsername() {
		return this.username;
	}


	/**
	 * Set score for this High-Score. Can't be negative.
	 *
	 * @param score Score.
	 */
	public void setScore(final long score) {
		if (score < 0) {
			throw new IllegalArgumentException("Score is lower than 0.");
		}
		this.score = score;
	}


	/**
	 * Set timestamp for this High-Score. Can't be 0 or negative.
	 *
	 * @param timestamp date represented in milliseconds.
	 */
	public void setTimestamp(final long timestamp) {
		if (timestamp <= 0) {
			throw new IllegalArgumentException("Timestamp is lower or equal to 0.");
		}
		this.timestamp = timestamp;
	}


	/**
	 * Set user's username. Can't be null.
	 *
	 * @param username Username of the user.
	 */
	public void setUsername(final String username) {
		if (username == null) {
			throw new NullPointerException("Username is null.");
		}
		this.username = username;
	}


	@Override
	public int compareTo(final HighScore o) {
		final int scoreCompare = Long.compare(o.getScore(), getScore());

		return scoreCompare == 0 ? Long.compare(getTimestamp(), o.getTimestamp()) : scoreCompare;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final HighScore highScore = (HighScore) o;
		return this.score == highScore.score && this.timestamp == highScore.timestamp && Objects
				.equals(this.username, highScore.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.score, this.timestamp, this.username);
	}
}
