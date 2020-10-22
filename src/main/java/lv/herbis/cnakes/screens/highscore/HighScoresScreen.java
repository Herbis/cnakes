package lv.herbis.cnakes.screens.highscore;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.constants.SoundConstants;
import lv.herbis.cnakes.context.ContextItems;
import lv.herbis.cnakes.draw.Drawing;
import lv.herbis.cnakes.entities.Pagination;
import lv.herbis.cnakes.listeners.HighScoreScreenControllerListener;
import lv.herbis.cnakes.listeners.HighScoreScreenKeyListener;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.screens.CnakesScreen;
import lv.herbis.cnakes.sound.SoundManager;
import lv.herbis.cnakes.tools.ConversionUtil;
import lv.herbis.cnakes.tools.DataUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;


public class HighScoresScreen implements CnakesScreen {
	private static final Logger LOG = LogManager.getLogger(HighScoresScreen.class);

	private static final float DRAW_ROWS = 14f;
	private static final int LINES_PER_PAGE = 10;
	private static final String TITLE_TEXT = "High Scores!";

	private final CnakesConfiguration configuration;
	private final long windowId;
	private final SoundManager soundManager;
	private int screenWidthCenterScaled;
	private int screenHeightScaled;

	private final DateTimeFormatter hsDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


	private HighScores highScores;
	private List<HighScoreLine> highScoreLines;

	private float titleLocation;

	private final Drawing drawing;
	private final ContextItems contextItems;

	private Pagination pagination;
	private int currentPage = 1;
	private String pageText;
	private float pageTextYLocation;

	public HighScoresScreen(final ContextItems contextItems) {
		this.contextItems = contextItems;
		this.configuration = contextItems.getConfiguration();
		this.windowId = contextItems.getWindowId();
		this.drawing = contextItems.getDrawing();
		this.soundManager = contextItems.getSoundManager();
		initConfiguration();
	}

	private void initConfiguration() {
		final int gameScale = this.configuration.getVideo().getScale();
		final int screenWidth = this.configuration.getVideo().getResolution().getHorizontal();
		final int screenHeight = this.configuration.getVideo().getResolution().getVertical();
		final int screenWidthScaled = screenWidth / gameScale;
		this.screenHeightScaled = screenHeight / gameScale;
		this.screenWidthCenterScaled = screenWidthScaled / 2;
	}

	/**
	 * Initializes the screen.
	 */
	@Override
	public void initScreen() {
		LOG.debug("initScreen() of {} called.", HighScoresScreen.class.getName());
		initSounds();
		loadHighScores();
		initPagination();
		initScorePage();
		glfwSetKeyCallback(this.windowId, new HighScoreScreenKeyListener(this.pagination));
		final HighScoreScreenControllerListener controllerListener = new HighScoreScreenControllerListener(
				this.pagination);
		glfwSetJoystickCallback(controllerListener);
		this.contextItems.getControllerStatePublisher().setControllerListener(controllerListener);
		this.drawing.initFont("fonts/trs-million_rg.ttf");

	}

	private void initPagination() {
		this.pagination = new Pagination(BigDecimal.valueOf(this.highScores.getHighScoreCount())
												 .divide(BigDecimal.valueOf(LINES_PER_PAGE), RoundingMode.CEILING)
												 .intValue());
	}

	protected void initScorePage() {
		final float screenUnit = this.screenHeightScaled / DRAW_ROWS;
		this.titleLocation = this.screenHeightScaled;
		this.currentPage = this.pagination.getPage();

		final List<HighScore> highScoreList = this.highScores.getHighScoreList();
		final List<HighScoreLine> lines = new LinkedList<>();

		for (int i = 0; i < LINES_PER_PAGE; i++) {
			final int entryIndex = i + ((this.pagination.getPage() - 1) * LINES_PER_PAGE);

			if (entryIndex >= highScoreList.size()) {
				break;
			}

			final HighScore highScore = highScoreList.get(entryIndex);

			final float highScoreLocation = this.screenHeightScaled - (screenUnit * (i + 2));
			final String timeStamp = this.hsDateFormat
					.format(ConversionUtil.millisecondsToLocalDateTime(highScore.getTimestamp()));
			final HighScoreLine line = new HighScoreLine(
					String.format("%s: %s [%s]", highScore.getUsername(), highScore.getScore(), timeStamp),
					this.screenWidthCenterScaled, highScoreLocation, Color.BLUE);
			lines.add(line);
		}

		this.highScoreLines = lines;
		this.pageText = String.format("%d/%d", this.currentPage, this.pagination.getPageCount());
		this.pageTextYLocation = this.screenHeightScaled - (screenUnit * (DRAW_ROWS - 1));
	}

	private void initSounds() {
		this.soundManager.createSound(SoundConstants.GameplaySounds.BAD_ACTION_SOURCE,
									  SoundConstants.GameplaySounds.BAD_ACTION_PATH);
	}

	/**
	 * Loads High Scores from a file to the local class.
	 */
	public void loadHighScores() {
		this.highScores = DataUtil.loadHighScores(100);

		this.drawing.updateHighScores(this.highScores);
	}

	/**
	 * Renders all that needs to be rendered for this game.
	 */
	private void render() {
		this.drawing.drawText(TITLE_TEXT, 2, this.screenWidthCenterScaled, this.titleLocation, Color.CYAN, true);

		final List<HighScoreLine> lines = this.highScoreLines;
		for (final HighScoreLine line : lines) {
			this.drawing.drawText(line.getText(), 1, line.getLocationX(), line.getLocationY(), Color.BLUE, true);
		}

		this.drawing.drawText(this.pageText, 1, this.screenWidthCenterScaled, this.pageTextYLocation, Color.CYAN, true);
	}

	/**
	 * Updates the game.
	 */
	@Override
	public void update() {
		if (this.currentPage != this.pagination.getPage()) {
			initScorePage();
		}
		render();
	}

	static class HighScoreLine {
		private final String text;
		private final float locationX;
		private final float locationY;
		private final Color color;

		public HighScoreLine(final String text, final float locationX, final float locationY, final Color color) {
			this.text = text;
			this.locationX = locationX;
			this.locationY = locationY;
			this.color = color;
		}

		public String getText() {
			return this.text;
		}

		public float getLocationX() {
			return this.locationX;
		}

		public float getLocationY() {
			return this.locationY;
		}

		public Color getColor() {
			return this.color;
		}
	}

}
