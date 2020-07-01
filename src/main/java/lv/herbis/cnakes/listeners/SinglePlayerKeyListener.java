package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.movement.MovingDirections;
import lv.herbis.cnakes.status.GameStatus;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class SinglePlayerKeyListener extends GLFWKeyCallback {

	private final GameStatus game;
	private final long windowId;


	public SinglePlayerKeyListener(final GameStatus gameStatus, final long windowId) {
		this.game = gameStatus;
		this.windowId = windowId;

	}


	@Override
	public void invoke(final long window, final int key, final int scanCode, final int action, final int mods) {
		if (catchNotKeyPress(action) || catchMovement(key)) {
			return;
		}

		catchCommonAction(key);
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
			if (this.game.isPaused()) {
				this.game.end();
				glfwSetWindowShouldClose(this.windowId, true); // TODO should probably just go to main menu
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
				/* We can only start moving left, if we're not going right.*/
				if (MovingDirections.getP1Direction() != MovingDirections.RIGHT && MovingDirections
						.getP1Direction() != MovingDirections.LEFT) {
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.LEFT);
				}
				caught = true;
			} else if (key == GLFW_KEY_RIGHT) {
				/* We can only start moving right, if we're not going left.*/
				if (MovingDirections.getP1Direction() != MovingDirections.LEFT && MovingDirections
						.getP1Direction() != MovingDirections.RIGHT) {
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.RIGHT);
				}
				caught = true;
			} else if (key == GLFW_KEY_UP) {
				/* We can only start moving up, if we're not going down.*/
				if (MovingDirections.getP1Direction() != MovingDirections.DOWN && MovingDirections
						.getP1Direction() != MovingDirections.UP) {
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.UP);
				}
				caught = true;
			} else if (key == GLFW_KEY_DOWN) {
				/* We can only start moving down, if we're not going up.*/
				if (MovingDirections.getP1Direction() != MovingDirections.UP && MovingDirections
						.getP1Direction() != MovingDirections.DOWN) {
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.DOWN);
				}
				caught = true;
			}

			return caught;
		}

		return false;
	}

	private boolean catchNotKeyPress(final int action) {
		return action != GLFW_PRESS;
	}

}
