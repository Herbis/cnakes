package lv.herbis.cnakes.levels.singleplayer;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.controls.Direction;
import lv.herbis.cnakes.entities.PointCoordinates;
import lv.herbis.cnakes.entities.Timer;
import lv.herbis.cnakes.listeners.SinglePlayerKeyListener;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.staticaccess.GameRules;
import lv.herbis.cnakes.staticaccess.MovingDirections;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import lv.herbis.cnakes.tools.SerializationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import other.fontloader.Color4f;
import other.fontloader.FontTT;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class LevelScreen implements Runnable {
    private static final Logger LOG = LogManager.getLogger(LevelScreen.class);

    private static final String SAVE_FILE_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\My Games\\cnakes\\";
    private static final String HIGHSCORE_FILE = "classic.hs";
    private static final int GAME_LENGTH = 1;

    private static final int SCOREBOARD_HEIGHT = 100;

    private HighScores highScores;
    private final CnakesConfiguration configuration;

    private boolean fullScreen;
    private Integer monitor;

	private long windowId;
	private int screenWidth;
    private int screenHeight;
    private int gameBoundX;
    private int gameBoundY;
    private int gameScale;
    private static final int MOVE_EVERY_MS = 40; //40

    boolean halfCellReached = true;

    SinglePlayerGameStatus gameStatus;
    PointCoordinates head;
    PointCoordinates target;
    List<PointCoordinates> body;
    Random random;
    Date dateForTimer = new Date();

    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss.SSS");
    SimpleDateFormat hsDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    /**
     * time at last frame
     */
    long lastFrame;
    long lastDelta = 0;

    /**
     * frames per second
     */
    int fps;
    /**
     * last fps time
     */
    long lastFPS;

    /**
     * The fonts to draw to the screen
     */
    private FontTT gameFont;

    public LevelScreen(final CnakesConfiguration configuration) {
        this.configuration = configuration;
        initConfiguration();
    }

    /**
     * Cleans up (releases) the resources and destroys the window.
     */
    private void cleanUp() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
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
     * Draws the grid the snake is moving on (or under to be more precise) on the screen.
     */
    private void drawPlayGrid() {

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
    private void drawScoreboard() {


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


        drawTime();
    }


    public void drawSmoothTheSnakeMovement(final long pixelAmount) {
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
                        (head.Y * gameScaleF), (head.Y * gameScaleF) + gameScaleF, 3);

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
     */
    private void drawSnake() {
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
    private void drawTarget() {
        glColor3f(0.25f, 0.73f, 0.31f);
        glBegin(GL_QUADS);
        drawFilledSquare(target.X, target.Y);
        glEnd();
    }


    /**
     * Draws Game Time left on the screen.
     */
    public void drawTime() {
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
     * Starts the game loop, that keeps the game running.
     */
    private void gameLoop() {
        while (!glfwWindowShouldClose(windowId)) {
            glClear(GL_COLOR_BUFFER_BIT);
            update();

            glfwSwapBuffers(windowId);
            try {
                glfwPollEvents();
            } catch (final NullPointerException e) {
                System.exit(0);
            }
        }

    }


    /**
     * Calculate how many milliseconds have passed
     * since last frame.
     *
     * @return milliseconds passed since last frame
     */
    public int getDelta() {
        final long time = getTime();
        final int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }


    /**
     * Get the accurate system time
     *
     * @return The system time in milliseconds
     */
    public long getTime() {
        return System.nanoTime() / 1000000;
    }


    /**
     * Method that checks if the x,y matches any part of the tail.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean hitsTail(final long x, final long y) {

        for (int index = 0; index < body.size(); index++) {
            final PointCoordinates point = body.get(index);
            if (point.equals(new PointCoordinates(x, y))) {
                int bonus = 1;
                if (gameStatus.inBonus()) {
                    bonus = GameRules.POINTS_PER_POINT;
                }

                for (int till = index; till >= 0; till--) {
                    body.remove(till); // NOSONAR
                    gameStatus.addScore(GameRules.POINTS_PER_POINT + ((long) bonus * till));
                }

                gameStatus.setSnakeLength(body.size());

                return true;
            }
        }
        return true;
    }

    private void initConfiguration() {
        fullScreen = configuration.getVideo().getResolution().isFullScreen();
        screenWidth = configuration.getVideo().getResolution().getHorizontal();
        screenHeight = configuration.getVideo().getResolution().getVertical();
        monitor = configuration.getVideo().getMonitor();
        gameScale = configuration.getVideo().getScale();
        gameBoundX = screenWidth;
        gameBoundY = screenHeight - SCOREBOARD_HEIGHT;
    }


    /**
     * Initializes the display / window.
     */
    private void initDisplay() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set(); // NOSONAR

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

        final long fullScreenMonitor;
        if (fullScreen) {
            fullScreenMonitor = monitor == null ? glfwGetPrimaryMonitor() : glfwGetMonitors().get(monitor);
        } else {
            fullScreenMonitor = NULL;
        }


        windowId = glfwCreateWindow(screenWidth, screenHeight, "Cnakes", fullScreenMonitor, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Get the resolution of the primary monitor
        final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                windowId,
                (vidmode.width() - screenWidth) / 2,
                (vidmode.height() - screenHeight) / 2
        );

        glfwMakeContextCurrent(windowId);
        glfwSwapInterval(0);
        GL.createCapabilities();
    }


    /**
     * Initializes the game.
     */
    private void initGame() {
        loadHighScores();
        startGame();
        glfwSetKeyCallback(windowId, new SinglePlayerKeyListener(gameStatus));
    }


    /**
     * Initializes Open GL.
     */
    private void initGL() {

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, screenWidth, 0, screenHeight, -1, 1);
        glMatrixMode(GL_MODELVIEW);

        glClearColor(0, 0, 0, 1);

        glDisable(GL_DEPTH_TEST);
    }


    /**
     * Initializes textures that we will use.
     */
    private void initTextures() {
        try {
            gameFont = new FontTT(Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("fonts/telegrama_raw.otf")), 36, 0);
        } catch (final FontFormatException | IOException e) {
            LOG.error("Something went wrong while loading textures", e);
        }
    }


    /**
     * Loads High Scores from a file to the local class.
     */
    public void loadHighScores() {
        try {
            highScores = (HighScores) SerializationUtil.deserialize(SAVE_FILE_PATH, HIGHSCORE_FILE);
        } catch (final Exception e) {
            highScores = new HighScores(10);
        }
    }


    /**
     * Creates a new Snake;
     */
    public void newSnake() {
        head = new PointCoordinates(0, 0);
        body = new ArrayList<>();
    }


    /**
     * Creates a new target (bug).
     */
    public void newTarget() {
        if (target == null) {
            /* If we didn't have a target object already, create one. */
            target = new PointCoordinates(random.nextInt(gameBoundX / gameScale), random.nextInt(gameBoundY / gameScale));
        } else {
            /* Create a new target, but make sure it's not in the same spot as the old one. */
            PointCoordinates newTarget = new PointCoordinates(random.nextInt(gameBoundX / gameScale), random.nextInt(gameBoundY / gameScale));
            while (newTarget.equals(target)) {
                newTarget = new PointCoordinates(random.nextInt(gameBoundX / gameScale), random.nextInt(gameBoundY / gameScale));
            }

            target.setLocation(newTarget.X, newTarget.Y);
        }
    }


    /**
     * Renders all that needs to be rendered for this game.
     */
    private void render() {
        drawTarget();
        drawPlayGrid();
        drawScoreboard();
    }


    /**
     * Gets called when the thread starts (keeps the game running).
     */
    @Override
    public void run() {
        System.setProperty("org.lwjgl.librarypath", new File("native").getAbsolutePath());

        initDisplay();
        initGL();
        initGame();
        initTextures();

        gameLoop();
        cleanUp();
    }

    /**
     * Starts the playable game.
     */
    public void startGame() {
        MovingDirections.resetDirection(MovingDirections.PLAYER_1);

        gameStatus = new SinglePlayerGameStatus(Timer.minutesToMiliseconds(GAME_LENGTH)) {
            @Override
            public void afterEnd() {
                LOG.info("End of the game.");
                final HighScore highScore = new HighScore("Player 1", gameStatus.getScore());
                if (highScores.addHighScore(highScore)) {
                    LOG.debug("Adding to high-scores.");
                    try {
                        SerializationUtil.serialize(highScores, SAVE_FILE_PATH, HIGHSCORE_FILE);
                    } catch (final Exception e) {
                        LOG.error("Could not save high-score file.", e);
                    }
                } else {
                    LOG.debug("High-score was not added.");
                }
            }
        };

        head = new PointCoordinates(0, 0);
        random = new Random();
        body = new ArrayList<>();
        newTarget();
    }


    /**
     * Updates the game.
     */
    private void update() {

        /* Reset Target (bug) and Snake if the game has just been started. */
        if (gameStatus.hasJustStarted()) {
            MovingDirections.resetDirection(MovingDirections.PLAYER_1);
            newSnake();
            newTarget();
        }

        if (!gameStatus.isPaused() && gameStatus.isPlayed() && !gameStatus.hasEnded()) {

            /* Update only every few miliseconds. */
            if (lastDelta > MOVE_EVERY_MS) {
                lastDelta = getDelta();
                halfCellReached = false;

            } else if (lastDelta > MOVE_EVERY_MS / 2 && !halfCellReached) {
                updateSnakePosition();

                halfCellReached = true;
            }
            /* Calculate how much in the cell we should move. */ // 10(lastDelta) * 10(scale) / 40 (move_every_ms) = 2.5
            drawSmoothTheSnakeMovement(lastDelta * gameScale / MOVE_EVERY_MS);
        } else {
            drawSnake();

        }

        render();
        lastDelta += getDelta();
    }


    /**
     * Calculate the FPS and set it in the title bar
     */
    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }


    /**
     * Updates the snake position.
     */
    private void updateSnakePosition() {
        int direction = -1;
        if (head != null) {
            direction = MovingDirections.getDirection(MovingDirections.PLAYER_1);

            body.add(new PointCoordinates(head.X, head.Y));

            if (body.size() > gameStatus.getSnakeLength()) {
                body.remove(0);
            }

            if (direction == MovingDirections.RIGHT) {
                if (head.X + 1 < (gameBoundX / gameScale) && hitsTail(head.X + 1, head.Y)) { // maybe eliminate pointless game bound calculations?
                    head = new PointCoordinates(head.X + 1, head.Y);

                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.LEFT);
                }
            } else if (direction == MovingDirections.LEFT) {
                if (head.X - 1 >= 0 && hitsTail(head.X - 1, head.Y)) {
                    head = new PointCoordinates(head.X - 1, head.Y);
                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.RIGHT);
                }
            } else if (direction == MovingDirections.DOWN) {
                if (head.Y - 1 >= 0 && hitsTail(head.X, head.Y - 1)) {

                    head = new PointCoordinates(head.X, head.Y - 1);
                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.UP);
                }
            } else if (direction == MovingDirections.UP) {
                if (head.Y + 1 < (gameBoundY / gameScale) && hitsTail(head.X, head.Y + 1)) {
                    head = new PointCoordinates(head.X, head.Y + 1);
                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.DOWN);
                }
            }

            if (head.equals(target)) {
                gameStatus.collectBug();
                gameStatus.setInBonus(true);
                newTarget();
            }
        }

    }

}
