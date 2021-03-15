package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.context.ContextItems;
import lv.herbis.cnakes.screens.help.HelpScreen;

import java.awt.*;

public class HelpMenuItem implements MenuItem {
	private static final String NAME = "Help";

	private final ContextItems contextItems;

	public HelpMenuItem(final ContextItems contextItems) {
		this.contextItems = contextItems;
	}

	@Override
	public Color getColor() {
		return Color.GREEN;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean enter() {
		this.contextItems.getMenuNavigation().setPendingItem(new HelpScreen(this.contextItems));
		return true;
	}
}
