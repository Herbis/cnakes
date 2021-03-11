package lv.herbis.cnakes.status;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.entities.Timer;

import java.util.regex.Pattern;

public abstract class SinglePlayerGameStatus implements GameStatus {
	private static final int MAX_NAME_LENGTH = 24;
	private final Pattern validCharacters = Pattern.compile("^[a-zA-Z0-9\\s]$");

	private boolean beingPlayed;
	private boolean paused;
	private boolean justStarted;
	private boolean ended = false;
	private boolean highScoreNameEntered = true;

	private String highScoreName = "Player 1";

	private long score = 0;
	private long snakeLength = 5;
	private long bugsCollected = 0;
	private boolean inBonus = true;
	private Timer gameTimer;
	private long gameLength = 0;

	public SinglePlayerGameStatus(final CnakesConfiguration configuration, final long gameLength) {
		setHighScoreName(configuration.getUserName());
		this.gameLength = gameLength;
	}

	@Override
	public void start() {
		reset();
		this.beingPlayed = true;
		this.justStarted = true;
		this.gameTimer.start();
	}


	public long getScore() {
		return this.score;
	}


	public long getSnakeLength() {
		return this.snakeLength;
	}


	public long getBugsCollected() {
		return this.bugsCollected;
	}


	public void setBugsCollected(final long bugsCollected) {
		this.bugsCollected = bugsCollected;
	}


	public void collectBug() {
		this.bugsCollected++;
		this.snakeLength++;
	}

	public void setSnakeLength(final long length) {
		this.snakeLength = length;
	}

	public void setScore(final long score) {
		this.score = score;
	}


	public void addScore(final long add) {
		this.score += add;
	}


	@Override
	public void pause() {
		/* Only attempt to pause if the game is being played. */
		if (isBeingPlayed()) {
			this.paused = !this.paused;

			this.gameTimer.pause();
		}
	}


	@Override
	public boolean isPaused() {
		return this.paused;
	}

	@Override
	public boolean isBeingPlayed() {
		return this.beingPlayed;
	}


	public boolean inBonus() {
		return this.inBonus;
	}


	public void setInBonus(final boolean inBonus) {
		this.inBonus = inBonus;
	}

	@Override
	public void end() {
		if (!this.ended)
		{
			afterEnd();
		}

		this.ended = true;
		this.beingPlayed = false;
	}

	public abstract void afterEnd();

	@Override
	public boolean hasEnded() {
		if (this.ended) {
			return true;
		} else {
			if (this.gameTimer != null) {
				if (this.gameTimer.getTimeLeft() <= 0) {
					end();
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public void reset() {
		this.score = 0;
		this.snakeLength = 5;
		this.bugsCollected = 0;
		this.inBonus = true;
		this.ended = false;
		this.paused = false;
		this.beingPlayed = false;
		this.justStarted = false;
		this.highScoreNameEntered = false;

		this.gameTimer = new Timer(this.gameLength);
	}

	public Timer getTimer() {
		return this.gameTimer;
	}

	/**
	 * Will return true only once if the game has just been started.
	 */
	@Override
	public boolean hasJustStarted() {
		if (this.justStarted) {
			this.justStarted = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean isHighScoreNameEntered() {
		return highScoreNameEntered;
	}

	public void setHighScoreNameEntered(boolean highScoreNameEntered) {
		this.highScoreNameEntered = highScoreNameEntered;
	}

	@Override
	public String getHighScoreName() {
		return this.highScoreName;
	}

	@Override
	public void setHighScoreName(final String name) {
		if (name == null) {
			this.highScoreName = (new CnakesConfiguration()).getUserName();
		} else if (name.length() > MAX_NAME_LENGTH) {
			this.highScoreName = name.substring(0, MAX_NAME_LENGTH);
		} else {
			this.highScoreName = name;
		}
	}

	@Override
	public boolean isValidHighScoreCharacter(final char character) {
		return validCharacters.matcher("" + character).matches();
	}
}
