package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.movement.MenuNavigation;
import lv.herbis.cnakes.movement.MovingDirections;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MenuKeyListener extends GLFWKeyCallback {
	private final MenuNavigation navigation;

	public MenuKeyListener(final MenuNavigation navigation) {
		this.navigation = navigation;
	}

	@Override
	public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {

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

			return;
		} else if (key == GLFW_KEY_DOWN) {
			/* We can only start moving down, if we're not going up.*/

			return;
		}

		if (key == GLFW_KEY_ENTER) {

		} else if (key == GLFW_KEY_ESCAPE) {

			glfwDestroyWindow(0);
		}
	}
}
