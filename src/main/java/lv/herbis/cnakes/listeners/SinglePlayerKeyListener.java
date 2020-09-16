package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.movement.MovingDirections;
import lv.herbis.cnakes.status.GameStatus;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Objects;

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

	private void attemptToMoveLeft() {
		/* We can only start moving left, if we're not going right.*/
		if (MovingDirections.getP1Direction() != MovingDirections.RIGHT && MovingDirections
				.getP1Direction() != MovingDirections.LEFT) {
			MovingDirections.setP1DirectionAndPushToQueue(MovingDirections.LEFT);
		}
	}

	private void attemptToMoveRight() {
		/* We can only start moving right, if we're not going left.*/
		if (MovingDirections.getP1Direction() != MovingDirections.LEFT && MovingDirections
				.getP1Direction() != MovingDirections.RIGHT) {
			MovingDirections.setP1DirectionAndPushToQueue(MovingDirections.RIGHT);
		}
	}

	private void attemptToMoveUp() {
		/* We can only start moving up, if we're not going down.*/
		if (MovingDirections.getP1Direction() != MovingDirections.DOWN && MovingDirections
				.getP1Direction() != MovingDirections.UP) {
			MovingDirections.setP1DirectionAndPushToQueue(MovingDirections.UP);
		}
	}

	private void attemptToMoveDown() {
		/* We can only start moving down, if we're not going up.*/
		if (MovingDirections.getP1Direction() != MovingDirections.UP && MovingDirections
				.getP1Direction() != MovingDirections.DOWN) {
			MovingDirections.setP1DirectionAndPushToQueue(MovingDirections.DOWN);
		}
	}

	private boolean catchKeyPress(final int action) {
		return action == GLFW_PRESS;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else if (o == null || getClass() != o.getClass() || !super.equals(o)) {
			return false;
		}

		final SinglePlayerKeyListener that = (SinglePlayerKeyListener) o;
		return this.windowId == that.windowId && Objects.equals(this.game, that.game);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.game, this.windowId);
	}
}
