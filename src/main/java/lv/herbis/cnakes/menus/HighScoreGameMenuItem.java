package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class HighScoreGameMenuItem implements MenuItem {
	private static final Logger LOG = LogManager.getLogger(HighScoreGameMenuItem.class);
	private static final String NAME = "High Scores";

	private final long windowId;
	private final MenuNavigation menuNavigation;

	public HighScoreGameMenuItem(final MenuNavigation menuNavigation, final long windowId) {
		this.menuNavigation = menuNavigation;
		this.windowId = windowId;
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
		return true;
	}
}
