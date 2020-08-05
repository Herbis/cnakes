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
		this.ended = true;
		this.beingPlayed = false;
		afterEnd();
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
		this.inBonus = false;
		this.ended = false;
		this.paused = false;
		this.beingPlayed = false;
		this.justStarted = false;

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

}
