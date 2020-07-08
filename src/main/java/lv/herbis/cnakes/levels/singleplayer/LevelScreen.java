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

import javax.swing.filechooser.FileSystemView;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;


public class LevelScreen {
	private static final Logger LOG = LogManager.getLogger(LevelScreen.class);

	private static final String SAVE_FILE_PATH = FileSystemView.getFileSystemView().getDefaultDirectory()
			.getPath() + "\\My Games\\cnakes\\";
	private static final String HIGHSCORE_FILE = "classic.hs";
	private static final int GAME_LENGTH = 1;

	private HighScores highScores;
	private final CnakesConfiguration configuration;


	private long windowId;

	private int gameScale;
	private int gameSpeedMs;

	boolean halfCellReached = true;

	private final Drawing drawing;
	private SinglePlayerGameStatus gameStatus;
	private PointCoordinates head;
	private PointCoordinates target;
	private List<PointCoordinates> body;
	private Random random;

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


	public LevelScreen(final CnakesConfiguration configuration, final long windowId) {
		this.configuration = configuration;
		this.windowId = windowId;
		this.drawing = new Drawing(configuration);
		initConfiguration();
	}


	/**
	 * Calculate how many milliseconds have passed
	 * since last frame.
	 *
	 * @return milliseconds passed since last frame
	 */
	public int getDelta() {
		final long time = getTime();
		final int delta = (int) (time - this.lastFrame);
		this.lastFrame = time;

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
	public boolean hitsTail(final int x, final int y) {

		for (int index = 0; index < this.body.size(); index++) {
			final PointCoordinates point = this.body.get(index);
			if (point.equals(new PointCoordinates(x, y))) {
				int bonus = 1;
				if (this.gameStatus.inBonus()) {
					bonus = GameRules.POINTS_PER_POINT;
				}

				for (int till = index; till >= 0; till--) {
					this.body.remove(till); // NOSONAR
					this.gameStatus.addScore(GameRules.POINTS_PER_POINT + ((long) bonus * till));
				}

				this.gameStatus.setSnakeLength(this.body.size());

				return true;
			}
		}
		return true;
	}

	private void initConfiguration() {
		this.gameScale = this.configuration.getVideo().getScale();
		this.gameSpeedMs = this.configuration.getGameplay().getGameSpeed();
	}


	/**
	 * Initializes the game.
	 */
	public void initGame() {
		loadHighScores();
		startGame();
		glfwSetKeyCallback(this.windowId, new SinglePlayerKeyListener(this.gameStatus, this.windowId));
		this.drawing.initFont("fonts/trs-million_rg.ttf");

	}


	/**
	 * Loads High Scores from a file to the local class.
	 */
	public void loadHighScores() {
		try {
			this.highScores = (HighScores) SerializationUtil.deserialize(SAVE_FILE_PATH, HIGHSCORE_FILE);
		} catch (final Exception e) {
			this.highScores = new HighScores(10);
		}

		this.drawing.updateHighScores(this.highScores);
	}


	/**
	 * Creates a new Snake;
	 */
	public void newSnake() {
		this.head = new PointCoordinates(0, 0);
		this.body = new ArrayList<>();
	}


	/**
	 * Creates a new target / bug.
	 */
	public void newTarget() {
		PointCoordinates newTarget;
		do {
			newTarget = new PointCoordinates(this.random.nextInt(this.drawing.getPlayAreaXEndPoint()),
											 this.random.nextInt(this.drawing.getPlayAreaYEndPoint()));
		} while (newTarget.equals(this.target) || locationMatchesSnake(newTarget));

		if (this.target == null) {
			this.target = newTarget;
		} else {
			this.target.setLocation(newTarget.getX(), newTarget.getY());
		}
	}

	private boolean locationMatchesSnake(final PointCoordinates location) {
		if (location.equals(this.head)) {
			return true;
		}

		for (final PointCoordinates bodyLoc : this.body) {
			if (location.equals(bodyLoc)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Renders all that needs to be rendered for this game.
	 */
	private void render() {
		this.drawing.drawTarget(this.target);
		this.drawing.drawPlayGrid(this.head);
		this.drawing.drawScoreboard(this.gameStatus);
	}


	/**
	 * Starts the playable game.
	 */
	public void startGame() {
		MovingDirections.resetDirection(MovingDirections.PLAYER_1);

		this.gameStatus = new SinglePlayerGameStatus(Timer.minutesToMilliseconds(GAME_LENGTH)) {
			@Override
			public void afterEnd() {
				LOG.info("End of the game.");
				final HighScore highScore = new HighScore("Player 1", LevelScreen.this.gameStatus.getScore());
				if (LevelScreen.this.highScores.addHighScore(highScore)) {
					LOG.debug("Adding to high-scores.");
					try {
						SerializationUtil.serialize(LevelScreen.this.highScores, SAVE_FILE_PATH, HIGHSCORE_FILE);
					} catch (final Exception e) {
						LOG.error("Could not save high-score file.", e);
					}

					LevelScreen.this.drawing.updateHighScores(LevelScreen.this.highScores);
				} else {
					LOG.debug("High-score was not added.");
				}
			}
		};

		this.head = new PointCoordinates(0, 0);
		this.random = new SecureRandom();
		this.body = new ArrayList<>();
		newTarget();
	}


	/**
	 * Updates the game.
	 */
	public void update() {

		/* Reset Target (bug) and Snake if the game has just been started. */
		if (this.gameStatus.hasJustStarted()) {
			MovingDirections.resetDirection(MovingDirections.PLAYER_1);
			newSnake();
			newTarget();
		}

		if (!this.gameStatus.isPaused() && this.gameStatus.isBeingPlayed() && !this.gameStatus.hasEnded()) {

			/* Update only every few milliseconds. */
			if (this.lastDelta > this.gameSpeedMs) {
				this.lastDelta = getDelta();
				this.halfCellReached = false;

			} else if (this.lastDelta > this.gameSpeedMs / 2 && !this.halfCellReached) {
				updateSnakePosition();

				this.halfCellReached = true;
			}
			/* Calculate how much in the cell we should move. */ // 10(lastDelta) * 10(scale) / 40 (move_every_ms) = 2.5
			final int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);
			this.drawing.drawSnakeInMovement(this.head, this.body, direction,
											 this.lastDelta * this.gameScale / this.gameSpeedMs, this.halfCellReached);
		} else {
			this.drawing.drawSnake(this.head, this.body);

		}

		render();
		this.lastDelta += getDelta();
	}


	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - this.lastFPS > 1000) {
			this.fps = 0;
			this.lastFPS += 1000;
		}
		this.fps++;
	}


	/**
	 * Updates the snake position.
	 */
	private void updateSnakePosition() {
		int direction = -1;
		if (this.head != null) {
			direction = MovingDirections.getDirection(MovingDirections.PLAYER_1);

			this.body.add(new PointCoordinates(this.head.getX(), this.head.getY()));

			if (this.body.size() > this.gameStatus.getSnakeLength()) {
				this.body.remove(0);
			}

			if (direction == MovingDirections.RIGHT) {
				if (this.head.getX() + 1 < (this.drawing.getPlayAreaXEndPoint()) && hitsTail(this.head.getX() + 1,
																							 this.head.getY())) {
					this.head = new PointCoordinates(this.head.getX() + 1, this.head.getY());

				} else {
					this.gameStatus.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.LEFT);
				}
			} else if (direction == MovingDirections.LEFT) {
				if (this.head.getX() - 1 >= 0 && hitsTail(this.head.getX() - 1, this.head.getY())) {
					this.head = new PointCoordinates(this.head.getX() - 1, this.head.getY());
				} else {
					this.gameStatus.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.RIGHT);
				}
			} else if (direction == MovingDirections.DOWN) {
				if (this.head.getY() - 1 >= 0 && hitsTail(this.head.getX(), this.head.getY() - 1)) {

					this.head = new PointCoordinates(this.head.getX(), this.head.getY() - 1);
				} else {
					this.gameStatus.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.UP);
				}
			} else if (direction == MovingDirections.UP) {
				if (this.head.getY() + 1 < (this.drawing.getPlayAreaYEndPoint()) && hitsTail(this.head.getX(),
																							 this.head.getY() + 1)) {
					this.head = new PointCoordinates(this.head.getX(), this.head.getY() + 1);
				} else {
					this.gameStatus.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.DOWN);
				}
			}

			if (this.head.equals(this.target)) {
				this.gameStatus.collectBug();
				this.gameStatus.setInBonus(true);
				newTarget();
			}
		}

	}

}
