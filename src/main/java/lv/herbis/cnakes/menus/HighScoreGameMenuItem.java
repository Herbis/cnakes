package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.context.ContextItems;
import lv.herbis.cnakes.screens.highscore.HighScoresScreen;

import java.awt.*;

public class HighScoreGameMenuItem implements MenuItem {
	private static final String NAME = "High Scores";

	private final ContextItems contextItems;

	public HighScoreGameMenuItem(final ContextItems contextItems) {
		this.contextItems = contextItems;
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
		this.contextItems.getMenuNavigation().setPendingItem(new HighScoresScreen(this.contextItems));
		return true;
	}
}
