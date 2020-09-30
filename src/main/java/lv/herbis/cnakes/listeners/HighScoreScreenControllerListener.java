package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import lv.herbis.cnakes.controls.ControllerState;
import lv.herbis.cnakes.entities.Pagination;
import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;

public class HighScoreScreenControllerListener extends ControllerListener {

	private static final Logger LOG = LogManager.getLogger(HighScoreScreenControllerListener.class);

	private final Pagination pagination;
	private ControllerMapping p1ControllerMapping;


	public HighScoreScreenControllerListener(final Pagination pagination) {
		this.pagination = pagination;
		initGamePads();
	}

	@Override
	public void invoke(final int i, final int i1) {
		// This is for handling controller connects / disconnects if it's ever going to be implemented.
	}

	@Override
	public void invokeButtonStateChange(final int controllerId, final int buttonId, final ButtonState state) {
		LOG.debug("Button state change invoked controllerId: {}, buttonId: {}, state: {}.", controllerId, buttonId, state);

		if (GLFW_JOYSTICK_1 == controllerId) {
			processP1ControllerStateChange(buttonId, state);
		}
	}

	public void processP1ControllerStateChange(final int buttonId, final ButtonState state) {
		if (this.p1ControllerMapping == null) {
			return;
		}

		if (ButtonState.PRESSED.equals(state)) {
			if (this.p1ControllerMapping.getLeft() == buttonId) {
				this.pagination.previousPage();
			} else if (this.p1ControllerMapping.getRight() == buttonId) {
				this.pagination.nextPage();
			} else if (this.p1ControllerMapping.getBack() == buttonId || this.p1ControllerMapping
					.getCancel() == buttonId) {
				throw new MainMenu.ReturnToMenuRequest();
			}
		}
	}

	public void initGamePads() {
		LOG.debug("Initializing Game Controllers.");
		this.p1ControllerMapping = getControllerMappingForName(glfwGetJoystickName(GLFW_JOYSTICK_1));
	}
}
