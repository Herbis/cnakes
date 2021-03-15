package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.menus.MainMenu;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class HelpScreenKeyListener extends GLFWKeyCallback {

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
		if (key == GLFW_KEY_ESCAPE) {
			throw new MainMenu.ReturnToMenuRequest();
		}
	}

	@Override
	public boolean equals(final Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode());
	}
}
