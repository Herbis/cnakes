package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.controls.ControllerMapping;
import lv.herbis.cnakes.controls.ControllerState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWJoystickCallback;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;

public abstract class ControllerListener extends GLFWJoystickCallback {
	private static final Logger LOG = LogManager.getLogger(ControllerListener.class);
	protected static final float DEFAULT_CONTROLLER_AXIS_DEAD_ZONE = 0.6f;

	protected ControllerMapping p1ControllerMapping;
	protected ControllerState p1ControllerState;

	public abstract void processP1ControllerStateChange(final int buttonId, final ButtonState state);

	public abstract void processP1ControllerAxisStateChange(final float[] axisState);

	public void invokeButtonStateChange(final int controllerId, final int buttonId, final ButtonState state) {
		LOG.debug("Button state change invoked controllerId: {}, buttonId: {}, state: {}.", controllerId, buttonId,
				  state);

		if (GLFW_JOYSTICK_1 == controllerId) {
			processP1ControllerStateChange(buttonId, state);
		}
	}

	public void invokeAxisStateChange(final int controllerId, final float[] axisState)
	{
		if (GLFW_JOYSTICK_1 == controllerId) {
			processP1ControllerAxisStateChange(axisState);
		}
	}

	@Override
	public void invoke(final int i, final int i1) {
		// This is for handling controller connects / disconnects if it's ever going to be implemented.
	}

	public void initGamePads() {
		LOG.debug("Initializing Game Controllers in class: {}.", getClass().getName());
		this.p1ControllerMapping = getControllerMappingForName(glfwGetJoystickName(GLFW_JOYSTICK_1));
		this.p1ControllerState = new ControllerState();
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

	protected static AxisDirection getHorizontalAxisDirection(final float axisValue, final float deadZone)
	{
		if (Math.abs(axisValue) < deadZone)
		{
			return AxisDirection.NONE;
		}

		return axisValue < 0 ? AxisDirection.LEFT : AxisDirection.RIGHT;
	}

	protected static AxisDirection getVerticalAxisDirection(final float axisValue, final float deadZone)
	{
		if (Math.abs(axisValue) < deadZone)
		{
			return AxisDirection.NONE;
		}

		return axisValue < 0 ? AxisDirection.UP : AxisDirection.DOWN;
	}

	protected static AxisDirection determineAxisDirection(final AxisDirection horizontalDirection,
														  final AxisDirection verticalDirection,
														  final AxisDirection previousDirection)
	{
		if (AxisDirection.NONE.equals(verticalDirection)) {
			return horizontalDirection;
		} else if (AxisDirection.NONE.equals(horizontalDirection) || AxisDirection.NONE.equals(previousDirection)) {
			// Order priority is on Vertical Direction
			return verticalDirection;
		} else if (verticalDirection.equals(previousDirection)) {
			return horizontalDirection; // Alternate direction to horizontal
		} else if (horizontalDirection.equals(previousDirection)) {
			return verticalDirection; // Alternate direction to vertical
		} else if (isVerticalDirection(previousDirection)) {
			return verticalDirection; // Opposite of previous direction probably means intent to move there.
		} else if (isHorizontalDirection(previousDirection)) {
			return horizontalDirection; // Opposite of previous direction probably means intent to move there.
		}

		return AxisDirection.NONE;
	}

	protected static boolean isHorizontalDirection(final AxisDirection direction)
	{
		return AxisDirection.LEFT.equals(direction) || AxisDirection.RIGHT.equals(direction);
	}

	protected static boolean isVerticalDirection(final AxisDirection direction)
	{
		return AxisDirection.DOWN.equals(direction) || AxisDirection.UP.equals(direction);
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
