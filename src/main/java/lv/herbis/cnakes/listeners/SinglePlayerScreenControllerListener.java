package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.status.GameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static lv.herbis.cnakes.movement.MovingDirections.*;

public class SinglePlayerScreenControllerListener extends ControllerListener {

	protected static final String LOG_DIRECTION_UP_NAME = "Up";
	protected static final String LOG_DIRECTION_DOWN_NAME = "Down";
	protected static final String LOG_DIRECTION_LEFT_NAME = "Left";
	protected static final String LOG_DIRECTION_RIGHT_NAME = "Right";

	private static final Logger LOG = LogManager.getLogger(SinglePlayerScreenControllerListener.class);
	protected long p1LastDpadChangeNanoTime = 0L;
	protected boolean p1lastDpadMoveAttemptSuccessful = false;

	private final GameStatus game;

	public SinglePlayerScreenControllerListener(final GameStatus gameStatus) {
		this.game = gameStatus;
		initGamePads();
	}

	@Override
	public void processP1ControllerStateChange(final int buttonId, final ButtonState state) {
		if (this.p1ControllerMapping == null) {
			return;
		}

		if (ButtonState.PRESSED.equals(state)) {
			if (catchP1ButtonMovementChange(buttonId)) {
				LOG.debug("Caught Movement Change on Controller.");
			} else if (catchP1CommonAction(buttonId)) {
				LOG.debug("Caught Common action on Controller.");
			}
		}

		if (buttonId == this.p1ControllerMapping.getDown()) {
			this.p1ControllerState.setDownPressed(ButtonState.PRESSED.equals(state));
		} else if (buttonId == this.p1ControllerMapping.getUp()) {
			this.p1ControllerState.setUpPressed(ButtonState.PRESSED.equals(state));
		} else if (buttonId == this.p1ControllerMapping.getLeft()) {
			this.p1ControllerState.setLeftPressed(ButtonState.PRESSED.equals(state));
		} else if (buttonId == this.p1ControllerMapping.getRight()) {
			this.p1ControllerState.setRightPressed(ButtonState.PRESSED.equals(state));
		}
	}

	@Override
	protected void moveP1BasedOnCurrentAndPreviousAxisDirection(final AxisDirection direction,
																final AxisDirection previousDirection) {
		if (!previousDirection.equals(direction)) {
			if (AxisDirection.DOWN.equals(direction)) {
				attemptToMoveDown();
			} else if (AxisDirection.UP.equals(direction)) {
				attemptToMoveUp();
			} else if (AxisDirection.LEFT.equals(direction)) {
				attemptToMoveLeft();
			} else if (AxisDirection.RIGHT.equals(direction)) {
				attemptToMoveRight();
			}
		}
	}

	public boolean catchP1CommonAction(final int buttonId) {
		boolean caught = false;

		if (this.p1ControllerMapping.getCancel() == buttonId) {
			if (this.game.isPaused() || !this.game.isBeingPlayed()) {
				this.game.end();
				throw new MainMenu.ReturnToMenuRequest();
			}

			caught = true;
		} else if (this.p1ControllerMapping.getStart() == buttonId || this.p1ControllerMapping
				.getConfirm() == buttonId) {
			/* Only allow to start the game if the game is not being played or has ended. */
			if (!this.game.isBeingPlayed() || this.game.hasEnded()) {
				this.game.start();
			}
			caught = true;
		} else if (this.p1ControllerMapping.getBack() == buttonId) {
			this.game.pause();
			caught = true;
		}

		return caught;
	}

	public boolean catchP1ButtonMovementChange(final int buttonId) {
		if (isValidGameStateForMovementChange()) {
			/* Actions allowed only when the game has not been started or has not ended or is not paused. */
			boolean caught = false;
			if (this.p1ControllerMapping.getLeft() == buttonId) {
				moveP1LeftWithDPadIfRealistic();
				caught = true;
			} else if (this.p1ControllerMapping.getRight() == buttonId) {
				moveP1RightWithDPadIfRealistic();
				caught = true;
			} else if (this.p1ControllerMapping.getUp() == buttonId) {
				moveP1UpWithDPadIfRealistic();
				caught = true;
			} else if (this.p1ControllerMapping.getDown() == buttonId) {
				moveP1DownWithDPadIfRealistic();
				caught = true;
			}

			return caught;
		}

		return false;
	}

	protected void moveP1LeftWithDPadIfRealistic() {
		if (checkDpadChangeRealistic(this.p1lastDpadMoveAttemptSuccessful, this.p1LastDpadChangeNanoTime, LOG_DIRECTION_LEFT_NAME)) {
			this.p1lastDpadMoveAttemptSuccessful = attemptToMoveLeft();
			this.p1LastDpadChangeNanoTime = System.nanoTime();
		} else {
			this.p1lastDpadMoveAttemptSuccessful = false;
		}
	}

	protected void moveP1RightWithDPadIfRealistic() {
		if (checkDpadChangeRealistic(this.p1lastDpadMoveAttemptSuccessful, this.p1LastDpadChangeNanoTime, LOG_DIRECTION_RIGHT_NAME)) {
			this.p1lastDpadMoveAttemptSuccessful = attemptToMoveRight();
			this.p1LastDpadChangeNanoTime = System.nanoTime();
		} else {
			this.p1lastDpadMoveAttemptSuccessful = false;
		}
	}

	protected void moveP1UpWithDPadIfRealistic() {
		if (checkDpadChangeRealistic(this.p1lastDpadMoveAttemptSuccessful, this.p1LastDpadChangeNanoTime, LOG_DIRECTION_UP_NAME)) {
			this.p1lastDpadMoveAttemptSuccessful = attemptToMoveUp();
			this.p1LastDpadChangeNanoTime = System.nanoTime();
		} else {
			this.p1lastDpadMoveAttemptSuccessful = false;
		}
	}

	protected void moveP1DownWithDPadIfRealistic() {
		if (checkDpadChangeRealistic(this.p1lastDpadMoveAttemptSuccessful, this.p1LastDpadChangeNanoTime, LOG_DIRECTION_DOWN_NAME)) {
			this.p1lastDpadMoveAttemptSuccessful = attemptToMoveDown();
			this.p1LastDpadChangeNanoTime = System.nanoTime();
		} else {
			this.p1lastDpadMoveAttemptSuccessful = false;
		}
	}

	protected boolean isValidGameStateForMovementChange() {
		return this.game.isBeingPlayed() && !this.game.hasEnded() && !this.game.isPaused();
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
