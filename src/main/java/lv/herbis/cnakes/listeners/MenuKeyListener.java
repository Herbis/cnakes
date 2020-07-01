package lv.herbis.cnakes.listeners;


import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MenuKeyListener extends GLFWKeyCallback {
	private static final Logger LOG = LogManager.getLogger(MenuKeyListener.class);

	private final MenuNavigation navigation;
	private final long windowId;

	public MenuKeyListener(final MenuNavigation navigation, final long windowId) {
		this.navigation = navigation;
		this.windowId = windowId;
	}

	@Override
	public void invoke(final long window, final int key, final int scanCode, final int action, final int mods) {

		if (action != GLFW_PRESS) {
			return;
		}

		/* Actions allowed only when the game has not been started or has not ended or is not paused. */
		if (key == GLFW_KEY_LEFT) {
			/* We can only start moving left, if we're not going right.*/
			return;
		} else if (key == GLFW_KEY_RIGHT) {
			/* We can only start moving right, if we're not going left.*/
			return;
		} else if (key == GLFW_KEY_UP) {
			/* We can only start moving up, if we're not going down.*/
			this.navigation.moveUp();
			return;
		} else if (key == GLFW_KEY_DOWN) {
			/* We can only start moving down, if we're not going up.*/
			this.navigation.moveDown();
			return;
		} else if (key == GLFW_KEY_ENTER) {
			this.navigation.getActiveItem().enter();
		} else if (key == GLFW_KEY_ESCAPE) {
			LOG.debug("Exiting the game.");
			glfwSetWindowShouldClose(this.windowId, true);
		}
	}
}
