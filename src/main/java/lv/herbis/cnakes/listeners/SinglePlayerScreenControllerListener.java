package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.status.GameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static lv.herbis.cnakes.movement.MovingDirections.*;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;

public class SinglePlayerScreenControllerListener extends ControllerListener {

	private static final Logger LOG = LogManager.getLogger(SinglePlayerScreenControllerListener.class);

	private final GameStatus game;

	private ControllerMapping p1ControllerMapping;


	public SinglePlayerScreenControllerListener(final GameStatus gameStatus) {
		this.game = gameStatus;
		initGamePads();
	}

	@Override
	public void invoke(final int i, final int i1) {
		// This is for handling controller connects / disconnects if it's ever going to be implemented.
	}

	@Override
	public void invokeButtonStateChange(final int controllerId, final int buttonId, final ButtonState state) {
		LOG.debug("Button state change invoked controllerId: {}, buttonId: {}, state: {}.", controllerId, buttonId,
				  state);

		if (GLFW_JOYSTICK_1 == controllerId) {
			processP1ControllerStateChange(buttonId, state);
		}
	}

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
	}

	public boolean catchP1CommonAction(final int buttonId) {
		boolean caught = false;

		if (this.p1ControllerMapping.getBack() == buttonId || this.p1ControllerMapping.getCancel() == buttonId) {
			if (this.game.isPaused() || !this.game.isBeingPlayed()) {
				this.game.end();
				throw new MainMenu.ReturnToMenuRequest();
			}

			this.game.pause();
			caught = true;
		} else if (this.p1ControllerMapping.getStart() == buttonId || this.p1ControllerMapping
				.getConfirm() == buttonId) {
			/* Only allow to start the game if the game is not being played or has ended. */
			if (!this.game.isBeingPlayed() || this.game.hasEnded()) {
				this.game.start();
			}
			caught = true;
		}

		return caught;
	}

	public boolean catchP1ButtonMovementChange(final int buttonId) {
		if (this.game.isBeingPlayed() && !this.game.hasEnded() && !this.game.isPaused()) {
			/* Actions allowed only when the game has not been started or has not ended or is not paused. */
			boolean caught = false;
			if (this.p1ControllerMapping.getLeft() == buttonId) {
				attemptToMoveLeft();
				caught = true;
			} else if (this.p1ControllerMapping.getRight() == buttonId) {
				attemptToMoveRight();
				caught = true;
			} else if (this.p1ControllerMapping.getUp() == buttonId) {
				attemptToMoveUp();
				caught = true;
			} else if (this.p1ControllerMapping.getDown() == buttonId) {
				attemptToMoveDown();
				caught = true;
			}

			return caught;
		}

		return false;
	}

	public void initGamePads() {
		LOG.debug("Initializing Game Controllers.");
		this.p1ControllerMapping = getControllerMappingForName(glfwGetJoystickName(GLFW_JOYSTICK_1));
	}
}
