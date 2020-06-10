package lv.herbis.cnakes.status;

import lv.herbis.cnakes.entities.Timer;

public abstract class SinglePlayerGameStatus implements GameStatus {
	private boolean beingPlayed;
	private boolean paused;
	private boolean justStarted;
	private boolean ended = false;

	private long score = 0;
	private long snakeLength = 5;
	private long bugsCollected = 0;
	private boolean inBonus = false;
	private Timer gameTimer;
	private long gameLength = 0;

	public SinglePlayerGameStatus(final long gameLength) {
		this.gameLength = gameLength;
	}

	@Override
	public void start() {
		reset();
		beingPlayed = true;
		justStarted = true;
		gameTimer.start();
	}


	public long getScore() {
		return score;
	}


	public long getSnakeLength() {
		return snakeLength;
	}


	public long getBugsCollected() {
		return bugsCollected;
	}


	public void setBugsCollected(final long bugsCollected) {
		this.bugsCollected = bugsCollected;
	}


	public void collectBug() {
		bugsCollected++;
		snakeLength++;
	}

	public void setSnakeLength(final long length) {
		snakeLength = length;
	}

	public void setScore(final long score) {
		this.score = score;
	}


	public long addScore(final long add) {
		return score += add;
	}


	@Override
	public void pause() {
		/* Only attempt to pause if the game is being played. */
		if (isBeingPlayed()) {
			paused = !paused;

			gameTimer.pause();
		}
	}


	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public boolean isBeingPlayed() {
		return beingPlayed;
	}


	public boolean inBonus() {
		return inBonus;
	}


	public void setInBonus(final boolean inBonus) {
		this.inBonus = inBonus;
	}

	@Override
	public void end() {
		ended = true;
		beingPlayed = false;
		afterEnd();
	}

	public abstract void afterEnd();


	@Override
	public boolean hasEnded() {
		if (ended) {
			return true;
		} else {
			if (gameTimer != null) {
				if (gameTimer.getTimeLeft() <= 0) {
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
		score = 0;
		snakeLength = 5;
		bugsCollected = 0;
		inBonus = false;
		ended = false;
		paused = false;
		beingPlayed = false;
		justStarted = false;

		gameTimer = new Timer(gameLength);
	}

	public Timer getTimer() {
		return gameTimer;
	}

	/**
	 * Will return true only once if the game has just been started.
	 */
	@Override
	public boolean hasJustStarted() {
		if (justStarted) {
			justStarted = false;
			return true;
		} else {
			return false;
		}
	}

}
