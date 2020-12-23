package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.status.GameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static lv.herbis.cnakes.movement.MovingDirections.*;

public class SinglePlayerScreenControllerListener extends ControllerListener {

	private static final Logger LOG = LogManager.getLogger(SinglePlayerScreenControllerListener.class);
	private static final long MIN_DPAD_TIME_DIFFERENCE = 80_000_000; // 80ms
	private long lastDpadChangeNanoTime = 0L;
	private boolean lastDpadMoveAttemptSuccessful = false;

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
		if (this.game.isBeingPlayed() && !this.game.hasEnded() && !this.game.isPaused()) {
			/* Actions allowed only when the game has not been started or has not ended or is not paused. */
			boolean caught = false;
			if (this.p1ControllerMapping.getLeft() == buttonId) {
				if (checkDpadChangeRealistic("Left")) {
					this.lastDpadMoveAttemptSuccessful = attemptToMoveLeft();
					this.lastDpadChangeNanoTime = System.nanoTime();
				}
				caught = true;
			} else if (this.p1ControllerMapping.getRight() == buttonId) {
				if (checkDpadChangeRealistic("Right")) {
					this.lastDpadMoveAttemptSuccessful = attemptToMoveRight();
					this.lastDpadChangeNanoTime = System.nanoTime();
				} else {
					this.lastDpadMoveAttemptSuccessful = false;
				}

				caught = true;
			} else if (this.p1ControllerMapping.getUp() == buttonId) {
				if (checkDpadChangeRealistic("Up")) {
					this.lastDpadMoveAttemptSuccessful = attemptToMoveUp();
					this.lastDpadChangeNanoTime = System.nanoTime();
				} else {
					this.lastDpadMoveAttemptSuccessful = false;
				}

				caught = true;
			} else if (this.p1ControllerMapping.getDown() == buttonId) {
				if (checkDpadChangeRealistic("Down")) {
					this.lastDpadMoveAttemptSuccessful = attemptToMoveDown();
					this.lastDpadChangeNanoTime = System.nanoTime();
				} else {
					this.lastDpadMoveAttemptSuccessful = false;
				}


				caught = true;
			}

			return caught;
		}

		return false;
	}

	protected boolean checkDpadChangeRealistic(final String direction) {
		final long now = System.nanoTime();
		final long difference = now - this.lastDpadChangeNanoTime;
		if (this.lastDpadMoveAttemptSuccessful && difference < MIN_DPAD_TIME_DIFFERENCE) {
			LOG.debug(
					"DENIED to move {} but the time difference ({}ms) between dpad changes was too small, " + "and successful attempt already made recently.",
					direction, difference / 1_000_000);
			return false;
		} else {
			LOG.debug("ALLOWED to move {} the time difference ({}ms) between dpad changes was large enough.", direction,
					  difference / 1_000_000);
			return true;
		}
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
