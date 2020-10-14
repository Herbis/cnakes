package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.context.ContextItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class ExitGameMenuItem implements MenuItem {
	private static final Logger LOG = LogManager.getLogger(ExitGameMenuItem.class);
	private static final String NAME = "EXIT";

	private final long windowId;

	public ExitGameMenuItem(final ContextItems contextItems) {
		this.windowId = contextItems.getWindowId();
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean enter() {
		LOG.debug("Exiting the game. Window {}", this.windowId);
		glfwSetWindowShouldClose(this.windowId, true);
		return true;
	}
}
