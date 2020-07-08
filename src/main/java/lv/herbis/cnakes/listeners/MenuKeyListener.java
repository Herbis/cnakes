package lv.herbis.cnakes.listeners;


import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class MenuKeyListener extends GLFWKeyCallback {
	private static final Logger LOG = LogManager.getLogger(MenuKeyListener.class);

	private final MenuNavigation navigation;
	private final long windowId;

	public MenuKeyListener(final MenuNavigation navigation, final long windowId) {
		this.navigation = navigation;
		this.windowId = windowId;
	}

	private boolean catchKeyPress(final int action) {
		return action == GLFW_PRESS;
	}

	@Override
	public void invoke(final long window, final int key, final int scanCode, final int action, final int mods) {
		if (catchKeyPress(action)) {
			processKeyPress(key);
		}
	}

	private void processKeyPress(final int key) {
		if (key == GLFW_KEY_LEFT) {
			this.navigation.moveLeft();
		} else if (key == GLFW_KEY_RIGHT) {
			this.navigation.moveRight();
		} else if (key == GLFW_KEY_UP) {
			this.navigation.moveUp();
		} else if (key == GLFW_KEY_DOWN) {
			this.navigation.moveDown();
		} else if (key == GLFW_KEY_ENTER) {
			this.navigation.getActiveItem().enter();
		} else if (key == GLFW_KEY_ESCAPE) {
			LOG.debug("Exiting the game.");
			glfwSetWindowShouldClose(this.windowId, true);
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		final MenuKeyListener that = (MenuKeyListener) o;
		return this.windowId == that.windowId && Objects.equals(this.navigation, that.navigation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.navigation, this.windowId);
	}
}
