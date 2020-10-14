package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.context.ContextItems;
import lv.herbis.cnakes.screens.singleplayer.SinglePlayerLevelScreen;

import java.awt.*;

public class StartGameMenuItem implements MenuItem {

	private static final String NAME = "START";

	private final ContextItems contextItems;

	public StartGameMenuItem(final ContextItems contextItems) {
		this.contextItems = contextItems;
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
		final SinglePlayerLevelScreen ls = new SinglePlayerLevelScreen(this.contextItems);
		this.contextItems.getMenuNavigation().setPendingItem(ls);

		return true;
	}
}
