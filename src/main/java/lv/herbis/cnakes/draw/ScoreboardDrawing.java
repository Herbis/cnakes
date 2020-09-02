package lv.herbis.cnakes.draw;

import lv.herbis.cnakes.entities.Timer;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import lv.herbis.cnakes.tools.ConversionUtil;

import java.awt.*;
import java.time.format.DateTimeFormatter;

import static org.lwjgl.opengl.GL11.*;

public class ScoreboardDrawing {

	private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("mm:ss.S");
	private final DateTimeFormatter hsDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

	private final float gameOverXLocation;
	private final float gameOverYLocation;


	private final float highScoreXLocation;
	private final float highScoreYLocation;

	private final float startGameXLocation;
	private final float startGameYLocation;

	private final float timerXLocation;
	private final float timerYLocation;

	private HighScore topHighScore;
	private String topHighScoreFormatted;


	public ScoreboardDrawing(final Drawing drawing) {
		this.drawing = drawing;

		final float xMiddle = drawing.getPlayAreaXEndPoint() / 2f;
		final float yMiddle = drawing.getPlayAreaYEndPoint() / 2f;

		this.bugsEatenIconXLocation = 1;
		this.bugsEatenIconYLocation = drawing.getPlayAreaYEndPoint() + 3f;
		this.snakeLengthIconXLocation = 1;
		this.snakeLengthIconYLocation = drawing.getPlayAreaYEndPoint() + 2f;
		this.scoreIconXLocation = 1;
		this.scoreIconYLocation = drawing.getPlayAreaYEndPoint() + 1f;


		this.bugsEatenTextXLocation = this.bugsEatenIconXLocation + 1.5f;
		this.bugsEatenTextYLocation = this.bugsEatenIconYLocation + 1.1f;
		this.snakeLengthTextXLocation = this.snakeLengthIconXLocation + 1.5f;
		this.snakeLengthTextYLocation = this.snakeLengthIconYLocation + 1.1f;
		this.scoreTextXLocation = this.scoreIconXLocation + 1.5f;
		this.scoreTextYLocation = this.scoreIconYLocation + 1.1f;

		this.playerNameXLocation = 1f;
		this.playerNameYLocation = drawing.getPlayAreaYEndPoint() + 5.1f;
		this.gamePausedXLocation = xMiddle;
		this.gamePausedYLocation = yMiddle;
		this.gameOverXLocation = xMiddle;
		this.gameOverYLocation = drawing.getPlayAreaYEndPoint() + 3.1f;

		this.highScoreXLocation = xMiddle;
		this.highScoreYLocation = drawing.getPlayAreaYEndPoint() + 4.1f;

		this.startGameXLocation = xMiddle;
		this.startGameYLocation = drawing.getPlayAreaYEndPoint() + 3.1f;

		this.timerXLocation = xMiddle;
		this.timerYLocation = drawing.getPlayAreaYEndPoint() + 3.1f;
	}

	/**
	 * Draws scoreboard on the screen.
	 */
	public void drawScoreboard(final SinglePlayerGameStatus gameStatus) {
		/* Draw score icons */
		glBegin(GL_QUADS);
		/* "Bugs Eaten" square */
		glColor3f(0.25f, 0.73f, 0.31f);
		this.drawing.drawFilledSquare(this.bugsEatenIconXLocation, this.bugsEatenIconYLocation);

		/* "Snake Length" square */
		glColor3f(0.55f, 0.01f, 0.31f);
		this.drawing.drawFilledSquare(this.snakeLengthIconXLocation, this.snakeLengthIconYLocation);

		/* "Score" square */
		glColor3f(1.35f, 0.44f, 2.55f);
		this.drawing.drawFilledSquare(this.scoreIconXLocation, this.scoreIconYLocation);

		glEnd();


		/* "Bugs Eaten" number */
		this.drawing.drawText(String.valueOf(gameStatus.getBugsCollected()), 1, this.bugsEatenTextXLocation,
							  this.bugsEatenTextYLocation, Color.YELLOW, false);

		/* "Snake Length" number */
		this.drawing.drawText(String.valueOf(gameStatus.getSnakeLength()), 1, this.snakeLengthTextXLocation,
							  this.snakeLengthTextYLocation, Color.YELLOW, false);

		/* "Score" number */
		this.drawing
				.drawText(String.valueOf(gameStatus.getScore()), 1, this.scoreTextXLocation, this.scoreTextYLocation,
						  Color.YELLOW, false);

		/* "Player name" text */
		this.drawing.drawText("Player 1", 1, this.playerNameXLocation, this.playerNameYLocation, Color.YELLOW, false);


		if (gameStatus.isPaused()) {
			this.drawing.drawText("PAUSED", 2, this.gamePausedXLocation, this.gamePausedYLocation, Color.YELLOW, true);
		}

		drawTime(gameStatus);
	}


	/**
	 * Draws Game Time left on the screen.
	 */
	public void drawTime(final SinglePlayerGameStatus gameStatus) {
		/* Draw numbers */
		final Timer currentTimer = gameStatus.getTimer();
		if (currentTimer == null) {
			/* If the current Timer is null, the game was never started. */
			this.drawing.drawText("Start the Game!", 2f, this.startGameXLocation, this.startGameYLocation, Color.GREEN,
								  true);
		} else {
			if (gameStatus.hasEnded()) {
				this.drawing.drawText("Game Over", 2f, this.gameOverXLocation, this.gameOverYLocation, Color.RED, true);
				if (this.topHighScore == null) {
					this.drawing.drawText("High score cannot be displayed.", 1f, this.highScoreXLocation,
										  this.highScoreYLocation, Color.WHITE, true);
				} else {
					this.drawing.drawText(String.format("Top High Score: %d (%s) %s", this.topHighScore.getScore(),
														this.topHighScore.getUsername(), this.topHighScoreFormatted),
										  1f, this.highScoreXLocation, this.highScoreYLocation, Color.WHITE, true);
				}
			} else {
				this.drawing.drawText(this.timeFormat.format(ConversionUtil.millisecondsToLocalDateTime(
						gameStatus.getTimer().getTimeLeft())), 2f, this.timerXLocation, this.timerYLocation, Color.RED,
									  true);
			}
		}
	}

	public void updateHighScores(final HighScores highScores) {
		this.topHighScore = highScores.getTopScore();
		if (this.topHighScore == null) {
			this.topHighScoreFormatted = "";
		} else {
			this.topHighScoreFormatted = this.hsDateFormat
					.format(ConversionUtil.millisecondsToLocalDateTime(this.topHighScore.getTimestamp()));
		}
	}

}
