package lv.herbis.cnakes.screens.highscore;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.constants.SoundConstants;
import lv.herbis.cnakes.draw.Drawing;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;


public class HighScoresScreen implements CnakesScreen {
	private static final Logger LOG = LogManager.getLogger(HighScoresScreen.class);

	private final CnakesConfiguration configuration;
	private final long windowId;
	private final SoundManager soundManager;
	private int screenWidth;
	private int screenWidthScaled;
	private int screenWidthCenterScaled;
	private int screenHeight;
	private int screenHeightScaled;

	private final DateTimeFormatter hsDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


	private HighScores highScores;

	private final Drawing drawing;
	private int gameScale;

	public HighScoresScreen(final CnakesConfiguration configuration, final long windowId,
							final SoundManager soundManager) {
		this.configuration = configuration;
		this.windowId = windowId;
		this.drawing = new Drawing(configuration);
		this.soundManager = soundManager;
		initConfiguration();
	}

	private void initConfiguration() {
		this.gameScale = this.configuration.getVideo().getScale();
		this.screenWidth = this.configuration.getVideo().getResolution().getHorizontal();
		this.screenHeight = this.configuration.getVideo().getResolution().getVertical();
		this.screenWidthScaled = this.screenWidth / this.gameScale;
		this.screenHeightScaled = this.screenHeight / this.gameScale;
		this.screenWidthCenterScaled = this.screenWidthScaled / 2;
	}


	/**
	 * Initializes the screen.
	 */
	public void initScreen() {
		LOG.debug("initScreen() of {} called.", HighScoresScreen.class.getName());
		initSounds();
		loadHighScores();
		glfwSetKeyCallback(this.windowId, new HighScoreScreenKeyListener());
		this.drawing.initFont("fonts/trs-million_rg.ttf");

	}

	private void initSounds() {
		this.soundManager.createSound(SoundConstants.GameplaySounds.BAD_ACTION_SOURCE,
									  SoundConstants.GameplaySounds.BAD_ACTION_PATH);
	}


	/**
	 * Loads High Scores from a file to the local class.
	 */
	public void loadHighScores() {
		this.highScores = DataUtil.loadHighScores(10);

		this.drawing.updateHighScores(this.highScores);
	}


	/**
	 * Renders all that needs to be rendered for this game.
	 */
	private void render() {

		final float screenUnit = this.screenHeightScaled / 12f;

		final float titleLocation = this.screenHeightScaled;
		this.drawing.drawText("High Scores!", 2, this.screenWidthCenterScaled, titleLocation, Color.CYAN, true);

		final List<HighScore> highScoreList = this.highScores.getHighScoreList();
		for (int i = 0; i < highScoreList.size(); i++) {
			final HighScore highScore = highScoreList.get(i);
			final float highScoreLocation = this.screenHeightScaled - (screenUnit * (i + 2));
			this.drawing.drawText(String.format("%s: %s [%s]", highScore.getUsername(), highScore.getScore(), this.hsDateFormat
					.format(ConversionUtil.millisecondsToLocalDateTime(highScore.getTimestamp()))), 1, this.screenWidthCenterScaled, highScoreLocation, Color.BLUE, true);
		}


	}


	/**
	 * Updates the game.
	 */
	public void update() {
		render();
	}

}
