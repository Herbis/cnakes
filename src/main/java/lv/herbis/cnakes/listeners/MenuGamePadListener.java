package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import lv.herbis.cnakes.controls.ControllerState;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class MenuGamePadListener extends GamePadListener {

	private static final Logger LOG = LogManager.getLogger(MenuGamePadListener.class);

	private ControllerState p1ControllerState;
	private ControllerMapping p1ControllerMapping;
	private final MenuNavigation menuNavigation;
	private long lastCheckNano;

	public MenuGamePadListener(final MenuNavigation menuNavigation) {
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
			} else if (this.p1ControllerMapping.getConfirm() == buttonId
					|| this.p1ControllerMapping.getStart() == buttonId) {
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

		/*final String gamePad2Name = glfwGetJoystickName(GLFW_JOYSTICK_2);
		final String gamePad3Name = glfwGetJoystickName(GLFW_JOYSTICK_3);
		final String gamePad4Name = glfwGetJoystickName(GLFW_JOYSTICK_4);
		final String gamePad5Name = glfwGetJoystickName(GLFW_JOYSTICK_5);
		final String gamePad6Name = glfwGetJoystickName(GLFW_JOYSTICK_6);
		final String gamePad7Name = glfwGetJoystickName(GLFW_JOYSTICK_7);
		final String gamePad8Name = glfwGetJoystickName(GLFW_JOYSTICK_8);
		final String gamePad9Name = glfwGetJoystickName(GLFW_JOYSTICK_9);
		final String gamePad10Name = glfwGetJoystickName(GLFW_JOYSTICK_10);
		final String gamePad11Name = glfwGetJoystickName(GLFW_JOYSTICK_11);
		final String gamePad12Name = glfwGetJoystickName(GLFW_JOYSTICK_12);
		final String gamePad13Name = glfwGetJoystickName(GLFW_JOYSTICK_13);
		final String gamePad14Name = glfwGetJoystickName(GLFW_JOYSTICK_14);
		final String gamePad15Name = glfwGetJoystickName(GLFW_JOYSTICK_15);
		final String gamePad16Name = glfwGetJoystickName(GLFW_JOYSTICK_16);*/

	}

	public void checkState() {
		final long currentTime = System.nanoTime();
		if (currentTime - this.lastCheckNano > 200_000_000) {
			this.lastCheckNano = currentTime;
		} else {
			return;
		}


		final ByteBuffer buttonStatus = glfwGetJoystickButtons(GLFW_JOYSTICK_1);

		if (buttonStatus == null) {
			return;
		}

		final StringBuilder stringBuilder = new StringBuilder("Controller: ");
		for (int i = 0; i < buttonStatus.capacity(); i++) {
			stringBuilder.append(i).append(":").append(buttonStatus.get(i)).append(",");
			// maybe use the same logic for
		}


		final FloatBuffer axisStatus = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
		if (axisStatus != null) {
			for (int i = 0; i < axisStatus.capacity(); i++) {
				stringBuilder.append("Axis(").append(i).append("):").append(axisStatus.get(i)).append(",");
			}
		}

		LOG.debug(stringBuilder);


		if (isButtonPressed(buttonStatus, this.p1ControllerMapping.getDown()) != this.p1ControllerState
				.isDownPressed()) {
			// trigger change
			if (this.p1ControllerState.isDownPressed()) {
				// trigger release event
			} else {
				// trigger press event
			}

			this.p1ControllerState.setDownPressed(!this.p1ControllerState.isDownPressed());
		}
	}

	private boolean isButtonPressed(final ByteBuffer buttonStatus, final int buttonId) {
		return buttonStatus.get(buttonId) == 1;
	}

	private ControllerMapping getControllerMappingForName(final String gamePadName) {
		// based on Xbox 360 controller
		final ControllerMapping mapping = new ControllerMapping();
		mapping.setName(gamePadName);
		mapping.setConfirm(0);
		mapping.setCancel(1);
		mapping.setBack(6);
		mapping.setStart(7);
		mapping.setLeft(13);
		mapping.setRight(11);
		mapping.setUp(10);
		mapping.setDown(12);
		mapping.setHorizontalAxisLeftStick(0);
		mapping.setVerticalAxisLeftStick(1);
		mapping.setHorizontalAxisRightStick(2);
		mapping.setVerticalAxisRightStick(3);

		return mapping;
	}
}
