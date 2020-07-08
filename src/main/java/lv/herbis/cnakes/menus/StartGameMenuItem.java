package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.levels.singleplayer.LevelScreen;
import lv.herbis.cnakes.movement.MenuNavigation;
import other.fontloader.Color4f;

public class StartGameMenuItem implements MenuItem {

	private static final String NAME = "START";

	private CnakesConfiguration configuration;
	private final MenuNavigation menuNavigation;
	private final long windowId;

	public StartGameMenuItem(final CnakesConfiguration configuration, final MenuNavigation menuNavigation,
							 final long windowId) {
		this.configuration = configuration;
		this.menuNavigation = menuNavigation;
		this.windowId = windowId;
	}

	@Override
	public Color4f getColor() {
		return Color4f.YELLOW;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean enter() {
		final LevelScreen ls = new LevelScreen(configuration, windowId);
		menuNavigation.setPendingItem(ls);

		return true;
	}
}