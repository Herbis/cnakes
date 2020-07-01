package lv.herbis.cnakes.menus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import other.fontloader.Color4f;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class ExitGameMenuItem implements MenuItem {
	private static final Logger LOG = LogManager.getLogger(ExitGameMenuItem.class);

	private static final String NAME = "EXIT";

	@Override
	public Color4f getColor() {
		return Color4f.RED;
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

	@Override
	public boolean back() {
		return false;
	}

	@Override
	public boolean up() {
		return false;
	}

	@Override
	public boolean down() {
		return false;
	}

	@Override
	public boolean left() {
		return false;
	}

	@Override
	public boolean right() {
		return false;
	}
}
