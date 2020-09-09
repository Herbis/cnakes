package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.entities.Pagination;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class HighScoreScreenKeyListener extends GLFWKeyCallback {

	private final Pagination pagination;

	public HighScoreScreenKeyListener(final Pagination pagination) {
		this.pagination = pagination;
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
			this.pagination.previousPage();
		} else if (key == GLFW_KEY_RIGHT) {
			this.pagination.nextPage();
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
