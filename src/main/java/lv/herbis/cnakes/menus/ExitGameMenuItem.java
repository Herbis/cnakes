package lv.herbis.cnakes.menus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class ExitGameMenuItem implements MenuItem {
	private static final Logger LOG = LogManager.getLogger(ExitGameMenuItem.class);

	private static final String NAME = "EXIT";

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
		LOG.debug("Exiting the game.");
		glfwSetWindowShouldClose(0, true);
		return true;
	}
}
