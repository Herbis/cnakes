package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import org.lwjgl.glfw.GLFWJoystickCallback;

public abstract class ControllerListener extends GLFWJoystickCallback {

	public abstract void invokeButtonStateChange(int gamePadId, int buttonId, ButtonState state);

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
