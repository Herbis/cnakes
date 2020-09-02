package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.entities.Pagination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class HighScoreScreenKeyListener extends GLFWKeyCallback {
	private static final Logger LOG = LogManager.getLogger(HighScoreScreenKeyListener.class);

	private final Pagination pagination;

	public HighScoreScreenKeyListener(final Pagination pagination)
	{
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
		} else if (key == GLFW_KEY_ESCAPE) {
			// TODO back to menu
		}
	}
}
