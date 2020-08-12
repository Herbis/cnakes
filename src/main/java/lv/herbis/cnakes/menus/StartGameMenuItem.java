package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.levels.singleplayer.SinglePlayerLevelScreen;
import lv.herbis.cnakes.movement.MenuNavigation;
import lv.herbis.cnakes.sound.SoundManager;

import java.awt.*;

public class StartGameMenuItem implements MenuItem {

	private static final String NAME = "START";

	private final CnakesConfiguration configuration;
	private final MenuNavigation menuNavigation;
	private final long windowId;
	private final SoundManager soundManager;

	public StartGameMenuItem(final CnakesConfiguration configuration, final MenuNavigation menuNavigation,
							 final long windowId, final SoundManager soundManager) {
		this.configuration = configuration;
		this.menuNavigation = menuNavigation;
		this.windowId = windowId;
		this.soundManager = soundManager;
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean enter() {
		final SinglePlayerLevelScreen ls = new SinglePlayerLevelScreen(configuration, windowId, this.soundManager);
		menuNavigation.setPendingItem(ls);

		return true;
	}
}
