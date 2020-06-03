package lv.herbis.cnakes.levels.singleplayer;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.draw.Drawing;
import lv.herbis.cnakes.entities.PointCoordinates;
import lv.herbis.cnakes.entities.Timer;
import lv.herbis.cnakes.listeners.SinglePlayerKeyListener;
import lv.herbis.cnakes.movement.MovingDirections;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.staticaccess.GameRules;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import lv.herbis.cnakes.tools.SerializationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
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

    private HighScores highScores;
    private final CnakesConfiguration configuration;

    private boolean fullScreen;
    private Integer monitor;

    private long windowId;
    private int screenWidth;
    private int screenHeight;
    private int gameScale;
    private static final int MOVE_EVERY_MS = 40; //40

    boolean halfCellReached = true;

    final Drawing drawing;
    SinglePlayerGameStatus gameStatus;
    PointCoordinates head;
    PointCoordinates target;
    List<PointCoordinates> body;
    Random random;

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

    public LevelScreen(final CnakesConfiguration configuration) {
        this.configuration = configuration;
        this.drawing = new Drawing(configuration);
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
            target = new PointCoordinates(random.nextInt(drawing.getPlayAreaXEndPoint()), random.nextInt(drawing.getPlayAreaYEndPoint()));
        } else {
            /* Create a new target, but make sure it's not in the same spot as the old one. */
            PointCoordinates newTarget = new PointCoordinates(random.nextInt(drawing.getPlayAreaXEndPoint()), random.nextInt(drawing.getPlayAreaYEndPoint()));
            while (newTarget.equals(target)) {
                newTarget = new PointCoordinates(random.nextInt(drawing.getPlayAreaXEndPoint()), random.nextInt(drawing.getPlayAreaYEndPoint()));
            }

            target.setLocation(newTarget.getX(), newTarget.getY());
        }
    }


    /**
     * Renders all that needs to be rendered for this game.
     */
    private void render() {
        drawing.drawTarget(target);
        drawing.drawPlayGrid(head);
        drawing.drawScoreboard(gameStatus, highScores);
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
        this.drawing.initFont("fonts/trs-million_rg.ttf");
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
            final int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);
            drawing.drawSnakeInMovement(head, body, direction, lastDelta * gameScale / MOVE_EVERY_MS, halfCellReached);
        } else {
            drawing.drawSnake(head, body);

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

            body.add(new PointCoordinates(head.getX(), head.getY()));

            if (body.size() > gameStatus.getSnakeLength()) {
                body.remove(0);
            }

            if (direction == MovingDirections.RIGHT) {
                if (head.getX() + 1 < (drawing.getPlayAreaXEndPoint()) && hitsTail(head.getX() + 1, head.getY())) { // maybe eliminate pointless game bound calculations?
                    head = new PointCoordinates(head.getX() + 1, head.getY());

                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.LEFT);
                }
            } else if (direction == MovingDirections.LEFT) {
                if (head.getX() - 1 >= 0 && hitsTail(head.getX() - 1, head.getY())) {
                    head = new PointCoordinates(head.getX() - 1, head.getY());
                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.RIGHT);
                }
            } else if (direction == MovingDirections.DOWN) {
                if (head.getY() - 1 >= 0 && hitsTail(head.getX(), head.getY() - 1)) {

                    head = new PointCoordinates(head.getX(), head.getY() - 1);
                } else {
                    gameStatus.setInBonus(false);

                    /* Set opposite direction. */
                    MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.UP);
                }
            } else if (direction == MovingDirections.UP) {
                if (head.getY() + 1 < (drawing.getPlayAreaYEndPoint()) && hitsTail(head.getX(), head.getY() + 1)) {
                    head = new PointCoordinates(head.getX(), head.getY() + 1);
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
