package lv.herbis.cnakes.draw;

import lv.herbis.cnakes.entities.Timer;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import other.fontloader.Color4f;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL11.*;

public class ScoreboardDrawing {

	private final Date dateForTimer = new Date();
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss.SSS");
	private final SimpleDateFormat hsDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private final Drawing drawing;

	private final float bugsEatenIconXLocation;
	private final float bugsEatenIconYLocation;
	private final float snakeLengthIconXLocation;
	private final float snakeLengthIconYLocation;
	private final float scoreIconXLocation;
	private final float scoreIconYLocation;

	private final float bugsEatenTextXLocation;
	private final float bugsEatenTextYLocation;
	private final float snakeLengthTextXLocation;
	private final float snakeLengthTextYLocation;
	private final float scoreTextXLocation;
	private final float scoreTextYLocation;
	private final float playerNameXLocation;
	private final float playerNameYLocation;

	private final float gamePausedXLocation;
	private final float gamePausedYLocation;

	private final float highScoreXLocation;
	private final float highScoreYLocation;

	private final float startGameXLocation;
	private final float startGameYLocation;

	private final float timerXLocation;
	private final float timerYLocation;

	public ScoreboardDrawing(final Drawing drawing) {
		this.drawing = drawing;

		final float xMiddle = drawing.getPlayAreaXEndPoint() / 2f;
		final float yMiddle = drawing.getPlayAreaYEndPoint() / 2f;

		bugsEatenIconXLocation = 1;
		bugsEatenIconYLocation = drawing.getPlayAreaYEndPoint() + 3f;
		snakeLengthIconXLocation = 1;
		snakeLengthIconYLocation = drawing.getPlayAreaYEndPoint() + 2f;
		scoreIconXLocation = 1;
		scoreIconYLocation = drawing.getPlayAreaYEndPoint() + 1f;


		bugsEatenTextXLocation = bugsEatenIconXLocation + 1.5f;
		bugsEatenTextYLocation = bugsEatenIconYLocation + 1.1f;
		snakeLengthTextXLocation = snakeLengthIconXLocation + 1.5f;
		snakeLengthTextYLocation = snakeLengthIconYLocation + 1.1f;
		scoreTextXLocation = scoreIconXLocation + 1.5f;
		scoreTextYLocation = scoreIconYLocation + 1.1f;

		playerNameXLocation = 1f;
		playerNameYLocation = drawing.getPlayAreaYEndPoint() + 5.1f;
		gamePausedXLocation = xMiddle;
		gamePausedYLocation = yMiddle;

		highScoreXLocation = xMiddle;
		highScoreYLocation = drawing.getPlayAreaYEndPoint() + 4.1f;

		startGameXLocation = xMiddle;
		startGameYLocation = drawing.getPlayAreaYEndPoint() + 3.1f;

		timerXLocation = xMiddle;
		timerYLocation = drawing.getPlayAreaYEndPoint() + 3.1f;
	}

	/**
	 * Draws scoreboard on the screen.
	 */
	public void drawScoreboard(final SinglePlayerGameStatus gameStatus, final HighScores highScores) {
		/* Draw score icons */
		glBegin(GL_QUADS);
		/* "Bugs Eaten" square */
		glColor3f(0.25f, 0.73f, 0.31f);
		drawing.drawFilledSquare(bugsEatenIconXLocation, bugsEatenIconYLocation);

		/* "Snake Length" square */
		glColor3f(0.55f, 0.01f, 0.31f);
		drawing.drawFilledSquare(snakeLengthIconXLocation, snakeLengthIconYLocation);

		/* "Score" square */
		glColor3f(1.35f, 0.44f, 2.55f);
		drawing.drawFilledSquare(scoreIconXLocation, scoreIconYLocation);

		glEnd();

		/* Draw numbers */
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_ALPHA);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		/* "Bugs Eaten" number */
		drawing.drawText(String.valueOf(gameStatus.getBugsCollected()), 1, bugsEatenTextXLocation, bugsEatenTextYLocation, Color4f.YELLOW, false);

		/* "Snake Length" number */
		drawing.drawText(String.valueOf(gameStatus.getSnakeLength()), 1, snakeLengthTextXLocation, snakeLengthTextYLocation, Color4f.YELLOW, false);

		/* "Score" number */
		drawing.drawText(String.valueOf(gameStatus.getScore()), 1, scoreTextXLocation, scoreTextYLocation, Color4f.YELLOW, false);

		/* "Player name" text */
		drawing.drawText("Player 1", 1, playerNameXLocation, playerNameYLocation, Color4f.YELLOW, false);


		if (gameStatus.isPaused()) {
			drawing.drawText("PAUSED", 2, gamePausedXLocation, gamePausedYLocation, Color4f.YELLOW, true);
		}
		glDisable(GL_TEXTURE_2D);

		drawTime(gameStatus, highScores);
	}


	/**
	 * Draws Game Time left on the screen.
	 */
	public void drawTime(final SinglePlayerGameStatus gameStatus, final HighScores highScores) {
		/* Draw numbers */
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_ALPHA);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		final Timer currentTimer = gameStatus.getTimer();
		if (currentTimer == null) {
			/* If the current Timer is null, the game was never started. */
			drawing.drawText("Start the Game!", 2f, startGameXLocation, startGameYLocation, Color4f.GREEN, true);
			//drawText("Start the Game!", 2, gameBoundX / 2.0f, gameBoundY + (gameScale * 3f), Color4f.GREEN, true);
		} else {
			if (gameStatus.hasEnded()) {
				/* If time left is equal to 0, the game has ended. */
				final HighScore topScore = highScores.getTopScore();
				drawing.drawText(String.format("Top High Scorre: %d (%s) %s", topScore.getScore(), highScores.getTopScore().getUsername(),
						hsDateFormat.format(new Date(topScore.getTimestamp()))), 1f, highScoreXLocation, highScoreYLocation, Color4f.WHITE, true);
			} else {
				/* If time left has a value higher than zero, lets use it, to show how much time player has left. */
				dateForTimer.setTime(gameStatus.getTimer().getTimeLeft());
				drawing.drawText(timeFormat.format(dateForTimer), 2f, timerXLocation, timerYLocation, Color4f.RED, true);
			}
		}

		glDisable(GL_TEXTURE_2D);
	}

}
