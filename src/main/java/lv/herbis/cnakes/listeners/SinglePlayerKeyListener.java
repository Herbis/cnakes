package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.status.GameStatus;
import org.lwjgl.glfw.GLFWKeyCallback;

import static lv.herbis.cnakes.movement.MovingDirections.*;
import static org.lwjgl.glfw.GLFW.*;

public class SinglePlayerKeyListener extends GLFWKeyCallback {

	private final GameStatus game;

	public SinglePlayerKeyListener(final GameStatus gameStatus) {
		this.game = gameStatus;
	}

	@Override
	public void invoke(final long window, final int key, final int scanCode, final int action, final int mods) {
		if (catchKeyPress(action)) {
			processKeyPress(key);
		}
	}

	private boolean processKeyPress(final int key) {
		return catchCommonAction(key) || catchMovement(key);
	}

	private boolean catchCommonAction(final int key) {
		boolean caught = false;

		if (key == GLFW_KEY_SPACE) {
			this.game.pause();
			caught = true;
		} else if (key == GLFW_KEY_ENTER) {
			/* Only allow to start the game if the game is not being played or has ended. */
			if (!this.game.isBeingPlayed() || this.game.hasEnded()) {
				this.game.start();
			}
			caught = true;
		} else if (key == GLFW_KEY_ESCAPE) {
			/* Only allow to exit if */
			if (this.game.isPaused() || !this.game.isBeingPlayed()) {
				this.game.end();
				throw new MainMenu.ReturnToMenuRequest();
			}
			caught = true;
		}

		return caught;
	}

	private boolean catchMovement(final int key) {
		if (this.game.isBeingPlayed() && !this.game.hasEnded() && !this.game.isPaused()) {
			/* Actions allowed only when the game has not been started or has not ended or is not paused. */
			boolean caught = false;
			if (key == GLFW_KEY_LEFT) {
				attemptToMoveLeft();
				caught = true;
			} else if (key == GLFW_KEY_RIGHT) {
				attemptToMoveRight();
				caught = true;
			} else if (key == GLFW_KEY_UP) {
				attemptToMoveUp();
				caught = true;
			} else if (key == GLFW_KEY_DOWN) {
				attemptToMoveDown();
				caught = true;
			}

			return caught;
		}

		return false;
	}

	private boolean catchKeyPress(final int action) {
		return action == GLFW_PRESS;
	}

	@Override
	public boolean equals(final Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
