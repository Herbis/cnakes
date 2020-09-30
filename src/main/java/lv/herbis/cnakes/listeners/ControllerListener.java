package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWJoystickCallback;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;

public abstract class ControllerListener extends GLFWJoystickCallback {
	private static final Logger LOG = LogManager.getLogger(ControllerListener.class);

	public abstract void invokeButtonStateChange(int gamePadId, int buttonId, ButtonState state);

	protected ControllerMapping p1ControllerMapping;

	public void initGamePads() {
		LOG.debug("Initializing Game Controllers in class: {}.", getClass().getName());
		this.p1ControllerMapping = getControllerMappingForName(glfwGetJoystickName(GLFW_JOYSTICK_1));
	}

	protected ControllerMapping getControllerMappingForName(final String gamePadName) {
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
