package lv.herbis.cnakes.draw;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.controls.Direction;
import lv.herbis.cnakes.entities.PointCoordinates;
import lv.herbis.cnakes.movement.MovingDirections;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import other.fontloader.Color4f;
import other.fontloader.FontTT;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Drawing {

	private static final Logger LOG = LogManager.getLogger(Drawing.class);

	private final CnakesConfiguration configuration;
	private int screenWidth;
	private int screenHeight;
	private int gameBoundX;
	private int gameBoundY;
	private int gameScale;
	private int scoreboardHeight;
	private int headThickness;

	private int playAreaXEndPoint;
	private int playAreaYEndPoint;

	private ScoreboardDrawing scoreboardDrawing;
	private FontTT gameFont;


	public Drawing(final CnakesConfiguration configuration) {
		this.configuration = configuration;
		initConfiguration();
	}

	public void initConfiguration() {
		this.screenWidth = this.configuration.getVideo().getResolution().getHorizontal();
		this.screenHeight = this.configuration.getVideo().getResolution().getVertical();
		this.gameScale = this.configuration.getVideo().getScale();
		this.gameBoundX = this.screenWidth;
		this.scoreboardHeight = this.gameScale * 5;
		this.gameBoundY = this.screenHeight - this.scoreboardHeight;
		this.headThickness = this.gameScale / 3;
		this.playAreaXEndPoint = this.gameBoundX / this.gameScale;
		this.playAreaYEndPoint = this.gameBoundY / this.gameScale;

		this.scoreboardDrawing = new ScoreboardDrawing(this);
	}

	public void initFont(final String fontLocation) {
		try {
			this.gameFont = new FontTT(
					Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream(fontLocation)),
					this.gameScale * 2, 0);
		} catch (final FontFormatException | IOException e) {
			LOG.error("Something went wrong while loading textures", e);
		}
	}

	/**
	 * Draws a filled square based on given coordinates.
	 * Set colour and call glBegin(GL_QUADS) before calling this, and call glEnd afterwards.
	 */
	void drawFilledSquare(final float x, final float y) {
		final float xLeft = x * this.gameScale;
		final float xRight = xLeft + this.gameScale;
		final float yBottom = y * this.gameScale;
		final float yTop = yBottom + this.gameScale;

		glVertex2f(xLeft, yTop); // top left
		glVertex2f(xLeft, yBottom); // bottom left
		glVertex2f(xRight, yBottom); // bottom right
		glVertex2f(xRight, yTop); // top right
	}

	/**
	 * Draws a filled square based on given coordinates.
	 * Set colour and call glBegin(GL_QUADS) before calling this, and call glEnd afterwards.
	 */
	private void drawFilledUnscaledSquare(final float xLeft, final float xRight, final float yBottom,
										  final float longTop) {
		glVertex2f(xLeft, longTop); // top left
		glVertex2f(xLeft, yBottom); // bottom left
		glVertex2f(xRight, yBottom); // bottom right
		glVertex2f(xRight, longTop); // top right
	}

	/**
	 * Draws a horizontal bright play grid line based on the direction of player.
	 *
	 * @param x1        x start coordinates
	 * @param x2        x end coordinates
	 * @param y         y coordinates
	 * @param direction player movement direction.
	 */
	private void drawHorizontalBrightPlayGridLineBasedOnDirection(final float x1, final float x2, final float y,
																  final int direction) {
		if (direction == Direction.DOWN || direction == Direction.UP) {
			/* draw regular line */
			drawPlayGridLine(x1, x2, y, y);
		} else {
			/* draw brighter */
			glColor3f(0.42f, 0.49f, 0.35f);
			drawPlayGridLine(x1, x2, y, y);
		}
	}

	/**
	 * Draws the grid the snake is moving on (or under to be more precise) on the screen.
	 *
	 * @param head Snake Head coordinates
	 */
	public void drawPlayGrid(final PointCoordinates head) {

		final long headXScaled = head.getX() * this.gameScale;
		final long headXScaledPlus = headXScaled + this.gameScale;
		final long headYScaled = head.getY() * this.gameScale;
		final long headYScaledPlus = headYScaled + this.gameScale;
		final int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);

		resetToBasicPlayGridLineColor();

		glBegin(GL_LINES);

		/* Vertical lines */
		for (int x = this.gameScale; x <= this.gameBoundX; x += this.gameScale) {
			if (this.configuration.getGameplay().isBrightenMovementLine() && (headXScaled == x || headXScaledPlus == x)) {
				drawVerticalBrightPlayGridLineBasedOnDirection(x, 0, this.gameBoundY, direction);
				resetToBasicPlayGridLineColor();
			} else {
				drawPlayGridLine(x, x, 0, this.gameBoundY);
			}
		}

		/* Horizontal lines */
		for (int y = this.gameScale; y <= this.gameBoundY; y += this.gameScale) {
			if (this.configuration.getGameplay().isBrightenMovementLine() && (headYScaled == y || headYScaledPlus == y)) {
				drawHorizontalBrightPlayGridLineBasedOnDirection(0, this.gameBoundX, y, direction);
				resetToBasicPlayGridLineColor();
			} else {
				drawPlayGridLine(0, this.gameBoundX, y, y);
			}
		}

		glEnd();
	}

	/**
	 * Draw a play grid line.
	 *
	 * @param x1 x start coordinates.
	 * @param x2 x end coordinates.
	 * @param y1 y start coordinates.
	 * @param y2 y end coordinates.
	 */
	private void drawPlayGridLine(final float x1, final float x2, final float y1, final float y2) {
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
	}

	public void drawScoreboard(final SinglePlayerGameStatus gameStatus) {
		this.scoreboardDrawing.drawScoreboard(gameStatus);
	}


	/**
	 * Draws the Snake on the screen.
	 *
	 * @param head snake head coordinates.
	 * @param body snake body coordinates.
	 */
	public void drawSnake(final PointCoordinates head, final List<PointCoordinates> body) {
		glColor3f(0.55f, 0.01f, 0.31f);

		/* Draw the head. */
		drawUnfilledSquare(head.getX(), head.getY(), this.headThickness);

		/* Draw the body. */
		glBegin(GL_QUADS); // Must
		for (final PointCoordinates bodyPart : body) {
			drawFilledSquare(bodyPart.getX(), bodyPart.getY());
		}
		glEnd();
	}

	private void drawSnakeHeadInMovement(final PointCoordinates head, final int direction, final long pixelAmount,
										 final boolean halfCellReached, final boolean drawExtraHeadBit) {
		glColor3f(0.55f, 0.01f, 0.31f);
		switch (direction) {
			case MovingDirections.DOWN:
				drawSnakeHeadInMovementToBottom(head, pixelAmount, halfCellReached, drawExtraHeadBit);
				break;
			case MovingDirections.UP:
				drawSnakeHeadInMovementToTop(head, pixelAmount, halfCellReached, drawExtraHeadBit);
				break;
			case MovingDirections.LEFT:
				drawSnakeHeadInMovementToLeft(head, pixelAmount, halfCellReached, drawExtraHeadBit);
				break;
			case MovingDirections.RIGHT:
				drawSnakeHeadInMovementToRight(head, pixelAmount, halfCellReached, drawExtraHeadBit);
				break;
			default:
				LOG.warn("Unknown direction {}, doing nothing.", direction);
		}
	}

	private void drawSnakeHeadInMovementToLeft(final PointCoordinates head, final long pixelAmount,
											   final boolean halfCellReached, final boolean includeNeck) {
		final float gameScaleF = this.gameScale;
		final float headXLeftScaled = head.getX() * gameScaleF;
		final float headXRightScaled = headXLeftScaled + gameScaleF;
		final float headYBottomScaled = head.getY() * gameScaleF;
		final float headYTopScaled = headYBottomScaled + gameScaleF;

		final float headLeft;
		final float headRight;
		final float headNeckLeft;
		final float headNeckRight;
		if (halfCellReached) {
			headLeft = headXLeftScaled - pixelAmount + gameScaleF;
			headRight = headXRightScaled - pixelAmount + gameScaleF;
			headNeckRight = headXRightScaled + gameScaleF;
		} else {
			headLeft = headXLeftScaled - pixelAmount;
			headRight = headXRightScaled - pixelAmount;
			headNeckRight = headXRightScaled;
		}
		headNeckLeft = headRight - 1;

		/* Draw the head. */
		drawUnfilledUnscaledSquare(headLeft, headRight, headYBottomScaled, headYTopScaled, this.headThickness);

		/* Draw the bit between the head and the body (neck). */
		if (includeNeck) {
			glBegin(GL_QUADS); // Must
			drawFilledUnscaledSquare(headNeckLeft, headNeckRight, headYBottomScaled, headYTopScaled);
			glEnd();
		}
	}

	private void drawSnakeHeadInMovementToRight(final PointCoordinates head, final long pixelAmount,
												final boolean halfCellReached, final boolean includeNeck) {
		final float gameScaleF = this.gameScale;
		final float headXLeftScaled = head.getX() * gameScaleF;
		final float headXRightScaled = headXLeftScaled + gameScaleF;
		final float headYBottomScaled = head.getY() * gameScaleF;
		final float headYTopScaled = headYBottomScaled + gameScaleF;

		final float headLeft;
		final float headRight;
		final float headNeckLeft;
		final float headNeckRight;

		if (halfCellReached) {
			headLeft = headXLeftScaled + pixelAmount - gameScaleF;
			headRight = headXRightScaled + pixelAmount - gameScaleF;
			headNeckLeft = headLeft - pixelAmount;
		} else {
			headLeft = headXLeftScaled + pixelAmount;
			headRight = headXRightScaled + pixelAmount;
			headNeckLeft = headXLeftScaled;
		}
		headNeckRight = headLeft + 1;


		/* Draw the head. */
		drawUnfilledUnscaledSquare(headLeft, headRight, headYBottomScaled, headYTopScaled, this.headThickness);

		/* Draw the bit between the head and the body (neck). */
		if (includeNeck) {
			glBegin(GL_QUADS); // Must
			drawFilledUnscaledSquare(headNeckLeft, headNeckRight, headYBottomScaled, headYTopScaled);
			glEnd();
		}
	}

	private void drawSnakeHeadInMovementToTop(final PointCoordinates head, final long pixelAmount,
											  final boolean halfCellReached, final boolean includeNeck) {
		final float gameScaleF = this.gameScale;
		final float headXLeftScaled = head.getX() * gameScaleF;
		final float headXRightScaled = headXLeftScaled + gameScaleF;
		final float headYBottomScaled = head.getY() * gameScaleF;
		final float headYTopScaled = headYBottomScaled + gameScaleF;

		final float headTop;
		final float headBottom;
		final float headNeckBottom;
		final float headNeckTop;

		if (halfCellReached) {
			headBottom = headYBottomScaled + pixelAmount - gameScaleF;
			headTop = headYTopScaled + pixelAmount - gameScaleF;
			headNeckBottom = headYBottomScaled - gameScaleF;
		} else {
			headBottom = headYBottomScaled + pixelAmount;
			headTop = headYTopScaled + pixelAmount;
			headNeckBottom = headYBottomScaled;
		}
		headNeckTop = headBottom + 1;


		/* Draw the head. */
		drawUnfilledUnscaledSquare(headXLeftScaled, headXRightScaled, headBottom, headTop, this.headThickness);

		/* Draw the bit between the head and the body (neck). */
		if (includeNeck) {
			glBegin(GL_QUADS); // Must
			drawFilledUnscaledSquare(headXLeftScaled, headXRightScaled, headNeckBottom, headNeckTop);
			glEnd();
		}
	}

	private void drawSnakeHeadInMovementToBottom(final PointCoordinates head, final long pixelAmount,
												 final boolean halfCellReached, final boolean includeNeck) {
		final float gameScaleF = this.gameScale;
		final float headXLeftScaled = head.getX() * gameScaleF;
		final float headXRightScaled = headXLeftScaled + gameScaleF;
		final float headYBottomScaled = head.getY() * gameScaleF;
		final float headYTopScaled = headYBottomScaled + gameScaleF;

		final float headTop;
		final float headBottom;
		final float headNeckBottom;
		final float headNeckTop;


		if (halfCellReached) {
			headBottom = headYBottomScaled - pixelAmount + gameScaleF;
			headTop = headYTopScaled - pixelAmount + gameScaleF;
			headNeckTop = headYTopScaled + gameScaleF;
		} else {
			headBottom = headYBottomScaled - pixelAmount;
			headTop = headYTopScaled - pixelAmount;
			headNeckTop = headYTopScaled;
		}
		headNeckBottom = headTop - 1;


		/* Draw the head. */
		drawUnfilledUnscaledSquare(headXLeftScaled, headXRightScaled, headBottom, headTop, this.headThickness);
		/* Draw the bit between the head and the body (neck). */
		if (includeNeck) {
			glBegin(GL_QUADS); // Must
			drawFilledUnscaledSquare(headXLeftScaled, headXRightScaled, headNeckBottom, headNeckTop);
			glEnd();
		}
	}

	public void drawSnakeInMovement(final PointCoordinates head, final List<PointCoordinates> body, final int direction,
									final long pixelAmount, final boolean halfCellReached) {
		final float gameScaleF = this.gameScale;
		final boolean drawExtraHeadBit = !body.isEmpty();

		drawSnakeHeadInMovement(head, direction, pixelAmount, halfCellReached, drawExtraHeadBit);

		/* Draw the body. */
		glBegin(GL_QUADS); // Must

		if (!body.isEmpty()) {
			/* We need to determine which snake part we need to smooth to. */
			final PointCoordinates smoothTo;
			if (body.size() > 1) {
				smoothTo = body.get(1); // Smooth to body part before the last one.
				int bodySize = body.size();
				if (halfCellReached) {
					bodySize = bodySize - 1;
				}
				for (int i = 1; i < bodySize; i++) {
					final PointCoordinates bodyPart = body.get(i);
					drawFilledSquare(bodyPart.getX(), bodyPart.getY());
				}

			} else {
				smoothTo = head; // Smooth to head.
			}
			final PointCoordinates lastBodyPart = body.get(0);


			glBegin(GL_QUADS); // Must
			if (smoothTo.getX() - lastBodyPart.getX() != 0) { // Smooth on the X axis
				if (smoothTo.getX() - lastBodyPart.getX() == 1) { // Smooth to left
					if (halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF) + pixelAmount - gameScaleF,
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF),
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF) + pixelAmount,
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF),
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF);
					}
				} else { // Smooth to right
					if (halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF), (lastBodyPart
														 .getX() * gameScaleF) + gameScaleF - pixelAmount + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF),
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF) - gameScaleF,
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF - pixelAmount,
												 (lastBodyPart.getY() * gameScaleF),
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF);
					}
				}
			} else if (smoothTo.getY() - lastBodyPart.getY() != 0) { // Smooth on the Y axis
				if (smoothTo.getY() - lastBodyPart.getY() == 1) { // Smooth up
					if (halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF),
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF) + pixelAmount - gameScaleF,
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF),
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF) + pixelAmount,
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF);
					}
				} else { // Smooth down
					if (halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF),
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF) - gameScaleF, (lastBodyPart
										.getY() * gameScaleF) + gameScaleF - pixelAmount + gameScaleF);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.getX() * gameScaleF),
												 (lastBodyPart.getX() * gameScaleF) + gameScaleF,
												 (lastBodyPart.getY() * gameScaleF),
												 (lastBodyPart.getY() * gameScaleF) + gameScaleF - pixelAmount);
					}
				}
			}
			glEnd();
		}
	}


	/**
	 * Draws the target (bug) on the screen).
	 */
	public void drawTarget(final PointCoordinates target) {
		glColor3f(0.25f, 0.73f, 0.31f);
		glBegin(GL_QUADS);
		drawFilledSquare(target.getX(), target.getY());
		glEnd();
	}

	public void drawText(final String text, final float size, final float x, final float y, final Color4f color,
						 final boolean centered) {
		this.gameFont.drawText(text, this.gameScale * size, x * this.gameScale, y * this.gameScale, 0, color, 0, 0, 0, centered);
	}

	public void drawOutlinedText(final String text, final float size, final float x, final float y, final Color4f color,
								 final Color4f outlineColor, final boolean centered) {
		this.gameFont.drawOutlinedText(text, this.gameScale * size, x * this.gameScale, y * this.gameScale, 0, color, outlineColor, 0, 0, 0,
									   centered);
	}

	/**
	 * Draws an unfilled square based on given coordinates.
	 * Does the glBegin(GL_LINE_STRIP) here too.
	 */
	private void drawUnfilledSquare(final float x, final float y, final int thickness) {
		glBegin(GL_LINE_STRIP);
		for (int lap = 1; lap <= thickness; lap++) {
			glVertex2f((x * this.gameScale) + lap, (y * this.gameScale) + this.gameScale - lap); // top left
			glVertex2f((x * this.gameScale) + lap, (y * this.gameScale) + (lap - 1)); // bottom left
			glVertex2f((x * this.gameScale) + this.gameScale - lap, y * this.gameScale + lap); // bottom right
			glVertex2f((x * this.gameScale) + this.gameScale - lap, (y * this.gameScale) + this.gameScale - lap); // top right
			glVertex2f((x * this.gameScale) + lap, (y * this.gameScale) + this.gameScale - lap); // top left
		}
		glEnd();
	}

	/**
	 * Draws an unfilled square based on given coordinates.
	 * Does the glBegin(GL_LINE_STRIP) here too.
	 */
	private void drawUnfilledUnscaledSquare(final float xLeft, final float xRight, final float yBottom,
											final float longTop, final int thickness) {
		glBegin(GL_LINE_STRIP);
		for (int lap = 1; lap <= thickness; lap++) {
			glVertex2f(xLeft + lap, longTop - lap); // top left
			glVertex2f(xLeft + lap, yBottom + (lap - 1)); // bottom left
			glVertex2f(xRight - lap, yBottom + lap); // bottom right
			glVertex2f(xRight - lap, longTop - lap); // top right
			glVertex2f(xLeft + lap, longTop - lap); // top left
		}
		glEnd();
	}

	/**
	 * Draws a vertical bright play grid line based on the direction of player.
	 *
	 * @param x         x coordinates.
	 * @param y1        y start coordinates.
	 * @param y2        y end coordinates.
	 * @param direction player movement direction.
	 */
	private void drawVerticalBrightPlayGridLineBasedOnDirection(final float x, final float y1, final float y2,
																final int direction) {
		if (direction == Direction.DOWN || direction == Direction.UP) {
			/* draw brighter */
			glColor3f(0.42f, 0.49f, 0.35f);
			drawPlayGridLine(x, x, y1, y2);
		} else {
			/* draw regular line */
			drawPlayGridLine(x, x, y1, y2);
		}
	}

	public int getPlayAreaXEndPoint() {
		return this.playAreaXEndPoint;
	}

	public int getPlayAreaYEndPoint() {
		return this.playAreaYEndPoint;
	}

	/**
	 * Reset color back to basic play grid line color.
	 */
	private void resetToBasicPlayGridLineColor() {
		glColor3f(0.22f, 0.29f, 0.15f);
	}

	int getScreenWidth() {
		return this.screenWidth;
	}

	int getScreenHeight() {
		return this.screenHeight;
	}

	int getGameBoundX() {
		return this.gameBoundX;
	}

	int getGameBoundY() {
		return this.gameBoundY;
	}

	int getGameScale() {
		return this.gameScale;
	}

	int getScoreboardHeight() {
		return this.scoreboardHeight;
	}

	FontTT getGameFont() {
		return this.gameFont;
	}

	public void updateHighScores(final HighScores highScores) {
		this.scoreboardDrawing.updateHighScores(highScores);
	}
}