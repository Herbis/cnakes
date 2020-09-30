package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import lv.herbis.cnakes.controls.ControllerState;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;

public class MenuControllerListener extends ControllerListener {

	private static final Logger LOG = LogManager.getLogger(MenuControllerListener.class);

	private ControllerState p1ControllerState;
	private ControllerMapping p1ControllerMapping;
	private final MenuNavigation menuNavigation;

	public MenuControllerListener(final MenuNavigation menuNavigation) {
		this.menuNavigation = menuNavigation;
		initGamePads();
	}

	@Override
	public void invoke(final int i, final int i1) {
		if (i == 10) {
			LOG.debug("Well, we're here.");
			// Should handle connect / disconnect
			// Default configuration from configuration file?
		}
	}

	@Override
	public void invokeButtonStateChange(final int gamePadId, final int buttonId, final ButtonState state) {
		LOG.debug("Button state change invoked gamePadId: {}, buttonId: {}, state: {}.", gamePadId, buttonId, state);

		if (GLFW_JOYSTICK_1 == gamePadId) {
			processGamePad1StateChange(buttonId, state);
		}
	}

	public void processGamePad1StateChange(final int buttonId, final ButtonState state) {
		if (this.p1ControllerMapping == null) {
			return;
		}

		if (ButtonState.PRESSED.equals(state)) {
			if (this.p1ControllerMapping.getDown() == buttonId) {
				this.menuNavigation.moveDown();
			} else if (this.p1ControllerMapping.getUp() == buttonId) {
				this.menuNavigation.moveUp();
			} else if (this.p1ControllerMapping.getConfirm() == buttonId || this.p1ControllerMapping
					.getStart() == buttonId) {
				this.menuNavigation.enterSelectedItem();
			} else if (this.p1ControllerMapping.getLeft() == buttonId) {
				this.menuNavigation.moveLeft();
			} else if (this.p1ControllerMapping.getRight() == buttonId) {
				this.menuNavigation.moveRight();
			}
		}
	}

	public void initGamePads() {
		LOG.debug("Initializing Game Pads.");
		this.p1ControllerMapping = getControllerMappingForName(glfwGetJoystickName(GLFW_JOYSTICK_1));
	}
}
