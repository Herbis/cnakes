package lv.herbis.cnakes.draw;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.controls.Direction;
import lv.herbis.cnakes.entities.PointCoordinates;
import lv.herbis.cnakes.entities.Timer;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.staticaccess.MovingDirections;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import other.fontloader.Color4f;
import other.fontloader.FontTT;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Drawing {

    private static final Logger LOG = LogManager.getLogger(Drawing.class);
    private static final int SCOREBOARD_HEIGHT = 100;

    private final CnakesConfiguration configuration;
    private int screenWidth;
    private int screenHeight;
    private int gameBoundX;
    private int gameBoundY;
    private int gameScale;

    private FontTT gameFont;

    private final Date dateForTimer = new Date();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss.SSS");
    private final SimpleDateFormat hsDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    public Drawing(final CnakesConfiguration configuration) {
        this.configuration = configuration;
        initConfiguration();
    }

    public void initConfiguration() {
        screenWidth = configuration.getVideo().getResolution().getHorizontal();
        screenHeight = configuration.getVideo().getResolution().getVertical();
        gameScale = configuration.getVideo().getScale();
        gameBoundX = screenWidth;
        gameBoundY = screenHeight - SCOREBOARD_HEIGHT;
    }

    public void initFont(final String fontLocation) {
        try {
            gameFont = new FontTT(Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream(fontLocation)), 36, 0);
        } catch (final FontFormatException | IOException e) {
            LOG.error("Something went wrong while loading textures", e);
        }
    }

    /**
     * Draws a filled square based on given coordinates.
     * Set colour and call glBegin(GL_QUADS) before calling this, and call glEnd afterwards.
     */
    private void drawFilledSquare(final float x, final float y) {
        glVertex2f(x * gameScale, y * gameScale + gameScale); // top left
        glVertex2f(x * gameScale, y * gameScale); // bottom left
        glVertex2f((x * gameScale + gameScale), y * gameScale); // bottom right
        glVertex2f((x * gameScale + gameScale), y * gameScale + gameScale); // top right
    }

    /**
     * Draws a filled square based on given coordinates.
     * Set colour and call glBegin(GL_QUADS) before calling this, and call glEnd afterwards.
     */
    private void drawFilledUnscaledSquare(final float xLeft, final float xRight, final float yBottom, final float longTop) {
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
    private void drawHorizontalBrightPlayGridLineBasedOnDirection(final float x1, final float x2, final float y, final int direction) {
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

        final long headXScaled = head.X * gameScale;
        final long headXScaledPlus = headXScaled + gameScale;
        final long headYScaled = head.Y * gameScale;
        final long headYScaledPlus = headYScaled + gameScale;
        final int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);

        resetToBasicPlayGridLineColor();

        glBegin(GL_LINES);

        /* Vertical lines */
        for (int x = gameScale; x <= gameBoundX; x += gameScale) {
            if (configuration.getGameplay().isBrightenMovementLine() && (headXScaled == x || headXScaledPlus == x)) {
                drawVerticalBrightPlayGridLineBasedOnDirection(x, 0, gameBoundY, direction);
                resetToBasicPlayGridLineColor();
            } else {
                drawPlayGridLine(x, x, 0, gameBoundY);
            }
        }

        /* Horizontal lines */
        for (int y = gameScale; y <= gameBoundY; y += gameScale) {
            if (configuration.getGameplay().isBrightenMovementLine() && (headYScaled == y || headYScaledPlus == y)) {
                drawHorizontalBrightPlayGridLineBasedOnDirection(0, gameBoundX, y, direction);
                resetToBasicPlayGridLineColor();
            } else {
                drawPlayGridLine(0, gameBoundX, y, y);
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

    /**
     * Draws scoreboard on the screen.
     */
    public void drawScoreboard(final SinglePlayerGameStatus gameStatus, final HighScores highScores) {
        /* Draw score icons */
        glBegin(GL_QUADS);
        /* "Bugs Eaten" square */
        glColor3f(0.25f, 0.73f, 0.31f);
        drawFilledSquare(1, (float) gameBoundY / gameScale + 6);

        /* "Snake Length" square */
        glColor3f(0.55f, 0.01f, 0.31f);
        drawFilledSquare(1, (float) gameBoundY / gameScale + 4);

        /* "Score" square */
        glColor3f(1.35f, 0.44f, 2.55f);
        drawFilledSquare(1, (float) gameBoundY / gameScale + 2);


        glEnd();

        /* Draw numbers */
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        /* "Bugs Eaten" number */
        gameFont.drawText("" + gameStatus.getBugsCollected(), 20, 25, screenHeight - 26.0f, 0, Color4f.YELLOW, 0, 0, 0, false);

        /* "Snake Length" number */
        gameFont.drawText("" + gameStatus.getSnakeLength(), 20, 25, screenHeight - 46.0f, 0, Color4f.YELLOW, 0, 0, 0, false);

        /* "Score" number */
        gameFont.drawText("" + gameStatus.getScore(), 20, 25, screenHeight - 66.0f, 0, Color4f.YELLOW, 0, 0, 0, false);

        /* "Player name" text */
        gameFont.drawText("Player 1", 20, 10, screenHeight - 4.0f, 0, Color4f.YELLOW, 0, 0, 0, false);

        if (gameStatus.isPaused()) {
            gameFont.drawText("PAUSED", 40, gameBoundX / 2.0f, screenHeight - 4.0f, 0, Color4f.YELLOW, 0, 0, 0, true);
        }
        glDisable(GL_TEXTURE_2D);


        drawTime(gameStatus, highScores);
    }

    public void drawSnakeInMovement(final PointCoordinates head, final List<PointCoordinates> body,
                                    final long pixelAmount, final boolean halfCellReached) {
        final int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);
        final float gameScaleF = gameScale;
        final boolean drawExtraHeadBit = !body.isEmpty();

        glColor3f(0.55f, 0.01f, 0.31f);
        if (direction == MovingDirections.DOWN) {
            if (halfCellReached) {
                /* Draw the head. */
                drawUnfilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScale,
                        (head.Y * gameScaleF) - pixelAmount + gameScaleF, (head.Y * gameScaleF) + gameScaleF - pixelAmount + gameScaleF, 3);
                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must

                    drawFilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                            (head.Y * gameScaleF) + gameScaleF - 1 - pixelAmount + gameScaleF, (head.Y * gameScaleF) + (gameScaleF * 2));
                    glEnd();
                }
            } else {
                drawUnfilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                        (head.Y * gameScaleF) - pixelAmount, (head.Y * gameScaleF) + gameScaleF - pixelAmount, 3);
                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                            (head.Y * gameScaleF) + gameScaleF - 1 - pixelAmount, (head.Y * gameScaleF) + gameScaleF);
                    glEnd();
                }
            }


        } else if (direction == MovingDirections.LEFT) {
            /* Draw the head. */
            if (halfCellReached) {
                drawUnfilledUnscaledSquare((head.X * gameScaleF) - pixelAmount + gameScaleF, (head.X * gameScaleF) + gameScaleF - pixelAmount + gameScaleF,
                        (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF, 3);
                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF) + gameScaleF - pixelAmount - 1 + gameScaleF, (head.X * gameScaleF) + gameScaleF + gameScaleF,
                            (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF);
                    glEnd();
                }
            } else {
                drawUnfilledUnscaledSquare((head.X * gameScaleF) - pixelAmount, (head.X * gameScaleF) + gameScaleF - pixelAmount,
                        (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF, 3);// TODO thickness based on scale / 2?

                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF) + gameScaleF - pixelAmount - 1, (head.X * gameScaleF) + gameScaleF,
                            (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF);
                    glEnd();
                }
            }

        } else if (direction == MovingDirections.RIGHT) {
            if (halfCellReached) {
                /* Draw the head. */
                drawUnfilledUnscaledSquare((head.X * gameScaleF) + pixelAmount - gameScaleF, (head.X * gameScaleF) + gameScaleF + pixelAmount - gameScaleF,
                        (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF, 3);

                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF) - gameScaleF, (head.X * gameScaleF) + 1 + pixelAmount - gameScaleF,
                            (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF);
                    glEnd();
                }
            } else {
                /* Draw the head. */
                drawUnfilledUnscaledSquare((head.X * gameScaleF) + pixelAmount, (head.X * gameScaleF) + gameScaleF + pixelAmount,
                        (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF, 3);
                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + 1 + pixelAmount,
                            (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF);
                    glEnd();
                }
            }

        } else if (direction == MovingDirections.UP) {
            if (halfCellReached) {
                /* Draw the head. */
                drawUnfilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                        (head.Y * gameScaleF) + pixelAmount - gameScaleF, (head.Y * gameScaleF) + gameScaleF + pixelAmount - gameScaleF, 3);

                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                            (head.Y * gameScaleF) - gameScaleF, (head.Y * gameScaleF) + 1 + pixelAmount - gameScaleF);
                    glEnd();
                }
            } else {
                /* Draw the head. */
                drawUnfilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                        (head.Y * gameScaleF) + pixelAmount, (head.Y * gameScaleF) + gameScaleF + pixelAmount, 3);

                if (drawExtraHeadBit) {
                    glBegin(GL_QUADS); // Must
                    drawFilledUnscaledSquare((head.X * gameScaleF), (head.X * gameScaleF) + gameScaleF,
                            (head.Y * gameScaleF), (head.Y * gameScaleF) + 1 + pixelAmount);
                    glEnd();
                }
            }
        }

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
                    drawFilledSquare(bodyPart.X, bodyPart.Y);
                }

            } else {
                smoothTo = head; // Smooth to head.
            }
            final PointCoordinates lastBodyPart = body.get(0);


            glBegin(GL_QUADS); // Must
            if (smoothTo.X - lastBodyPart.X != 0) { // Smooth on the X axis
                if (smoothTo.X - lastBodyPart.X == 1) { // Smooth to left
                    if (halfCellReached) {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF) + pixelAmount - gameScaleF, (lastBodyPart.X * gameScaleF) + gameScaleF,
                                (lastBodyPart.Y * gameScaleF), (lastBodyPart.Y * gameScaleF) + gameScaleF);
                    } else {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF) + pixelAmount, (lastBodyPart.X * gameScaleF) + gameScaleF,
                                (lastBodyPart.Y * gameScaleF), (lastBodyPart.Y * gameScaleF) + gameScaleF);
                    }
                } else { // Smooth to right
                    if (halfCellReached) {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF), (lastBodyPart.X * gameScaleF) + gameScaleF - pixelAmount + gameScaleF,
                                (lastBodyPart.Y * gameScaleF), (lastBodyPart.Y * gameScaleF) + gameScaleF);
                    } else {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF) - gameScaleF, (lastBodyPart.X * gameScaleF) + gameScaleF - pixelAmount,
                                (lastBodyPart.Y * gameScaleF), (lastBodyPart.Y * gameScaleF) + gameScaleF);
                    }
                }
            } else if (smoothTo.Y - lastBodyPart.Y != 0) { // Smooth on the Y axis
                if (smoothTo.Y - lastBodyPart.Y == 1) { // Smooth up
                    if (halfCellReached) {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF), (lastBodyPart.X * gameScaleF) + gameScaleF,
                                (lastBodyPart.Y * gameScaleF) + pixelAmount - gameScaleF, (lastBodyPart.Y * gameScaleF) + gameScaleF);
                    } else {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF), (lastBodyPart.X * gameScaleF) + gameScaleF,
                                (lastBodyPart.Y * gameScaleF) + pixelAmount, (lastBodyPart.Y * gameScaleF) + gameScaleF);
                    }
                } else { // Smooth down
                    if (halfCellReached) {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF), (lastBodyPart.X * gameScaleF) + gameScaleF,
                                (lastBodyPart.Y * gameScaleF) - gameScaleF, (lastBodyPart.Y * gameScaleF) + gameScaleF - pixelAmount + gameScaleF);
                    } else {
                        drawFilledUnscaledSquare((lastBodyPart.X * gameScaleF), (lastBodyPart.X * gameScaleF) + gameScaleF,
                                (lastBodyPart.Y * gameScaleF), (lastBodyPart.Y * gameScaleF) + gameScaleF - pixelAmount);
                    }
                }
            }
            glEnd();
        }
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
        drawUnfilledSquare(head.X, head.Y, 3);

        /* Draw the body. */
        glBegin(GL_QUADS); // Must
        for (final PointCoordinates bodyPart : body) {
            drawFilledSquare(bodyPart.X, bodyPart.Y);
        }
        glEnd();
    }


    /**
     * Draws the target (bug) on the screen).
     */
    public void drawTarget(final PointCoordinates target) {
        glColor3f(0.25f, 0.73f, 0.31f);
        glBegin(GL_QUADS);
        drawFilledSquare(target.X, target.Y);
        glEnd();
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
            gameFont.drawText("Start the Game!", 40, gameBoundX / 2.0f, screenHeight - 44.0f, 0, Color4f.GREEN, 0, 0, 0, true);
        } else {
            if (gameStatus.hasEnded()) {
                /* If time left is equal to 0, the game has ended. */
                gameFont.drawText("Game Over", 40, gameBoundX / 2.0f, screenHeight - 44.0f, 0, Color4f.RED, 0, 0, 0, true);
                final HighScore topScore = highScores.getTopScore();
                gameFont.drawText("Top High Score: " + topScore.getScore() + " ("
                                + highScores.getTopScore().getUsername() + ") "
                                + hsDateFormat.format(new Date(topScore.getTimestamp())),
                        20, gameBoundX / 2.0f, screenHeight - 14.0f, 0, Color4f.WHITE, 0, 0, 0, true);

            } else {
                /* If time left has a value higher than zero, lets use it, to show how much time player has left. */
                dateForTimer.setTime(gameStatus.getTimer().getTimeLeft());
                gameFont.drawText("" + timeFormat.format(dateForTimer), 40, gameBoundX / 2.0f, screenHeight - 44.0f, 0, Color4f.RED, 0, 0, 0, true);
            }
        }

        glDisable(GL_TEXTURE_2D);
    }


    /**
     * Draws an unfilled square based on given coordinates.
     * Does the glBegin(GL_LINE_STRIP) here too.
     */
    private void drawUnfilledSquare(final long x, final long y, final int thickness) {
        glBegin(GL_LINE_STRIP);
        for (int lap = 1; lap <= thickness; lap++) {
            glVertex2f((x * (float) gameScale) + lap, (y * (float) gameScale) + gameScale - lap); // top left
            glVertex2f((x * (float) gameScale) + lap, (y * (float) gameScale) + (lap - 1)); // bottom left
            glVertex2f((x * (float) gameScale) + gameScale - lap, y * (float) gameScale + lap); // bottom right
            glVertex2f((x * (float) gameScale) + gameScale - lap, (y * (float) gameScale) + gameScale - lap); // top right
            glVertex2f((x * (float) gameScale) + lap, (y * (float) gameScale) + gameScale - lap); // top left
        }
        glEnd();
    }

    /**
     * Draws an unfilled square based on given coordinates.
     * Does the glBegin(GL_LINE_STRIP) here too.
     */
    private void drawUnfilledUnscaledSquare(final float xLeft, final float xRight, final float yBottom, final float longTop, final int thickness) {
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
    private void drawVerticalBrightPlayGridLineBasedOnDirection(final float x, final float y1, final float y2, final int direction) {
        if (direction == Direction.DOWN || direction == Direction.UP) {
            /* draw brighter */
            glColor3f(0.42f, 0.49f, 0.35f);
            drawPlayGridLine(x, x, y1, y2);
        } else {
            /* draw regular line */
            drawPlayGridLine(x, x, y1, y2);
        }
    }

    /**
     * Reset color back to basic play grid line color.
     */
    private void resetToBasicPlayGridLineColor() {
        glColor3f(0.22f, 0.29f, 0.15f);
    }
}
