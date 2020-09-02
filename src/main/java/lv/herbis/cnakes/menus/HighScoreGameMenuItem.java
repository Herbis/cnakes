package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.movement.MenuNavigation;
import lv.herbis.cnakes.screens.highscore.HighScoresScreen;
import lv.herbis.cnakes.sound.SoundManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class HighScoreGameMenuItem implements MenuItem {
	private static final Logger LOG = LogManager.getLogger(HighScoreGameMenuItem.class);
	private static final String NAME = "High Scores";

	private final long windowId;
	private final MenuNavigation menuNavigation;
	private final CnakesConfiguration configuration;
	private final SoundManager soundManager;

	public HighScoreGameMenuItem(final CnakesConfiguration configuration, final MenuNavigation menuNavigation,
								 final long windowId, final SoundManager soundManager) {
		this.menuNavigation = menuNavigation;
		this.windowId = windowId;
		this.configuration = configuration;
		this.soundManager = soundManager;
	}

	@Override
	public Color getColor() {
		return Color.CYAN;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean enter() {
		LOG.debug("Showing high-scores. Window {}", this.windowId);

		getMenuNavigation().setPendingItem(new HighScoresScreen(configuration, windowId, soundManager));
		return true;
	}

	public MenuNavigation getMenuNavigation() {
		return menuNavigation;
	}
}
