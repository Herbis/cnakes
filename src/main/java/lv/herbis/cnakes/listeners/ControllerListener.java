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

	protected static final float DEFAULT_CONTROLLER_AXIS_DEAD_ZONE = 0.5f;
	protected static final long MIN_DPAD_TIME_DIFFERENCE = 80_000_000; // 80ms

	private static final Logger LOG = LogManager.getLogger(ControllerListener.class);

	protected ControllerMapping p1ControllerMapping;
	protected ControllerState p1ControllerState;

	public abstract void processP1ControllerStateChange(final int buttonId, final ButtonState state);

	protected abstract void moveP1BasedOnCurrentAndPreviousAxisDirection(final AxisDirection direction,
																	final AxisDirection previousDirection);

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

	public void processP1ControllerAxisStateChange(final float[] axisState) {

		final AxisDirection previousLsDirection = this.p1ControllerState.getLeftPadAxisDirection();
		final AxisDirection lsDirection = determineNewDirection(axisState,
																this.p1ControllerMapping.getHorizontalAxisLeftStick(),
																this.p1ControllerMapping.getVerticalAxisLeftStick(),
																previousLsDirection);
		this.p1ControllerState.setLeftPadAxisDirection(lsDirection);

		moveP1BasedOnCurrentAndPreviousAxisDirection(lsDirection, previousLsDirection);


		final AxisDirection previousRsDirection = this.p1ControllerState.getLeftPadAxisDirection();
		final AxisDirection rsDirection = determineNewDirection(axisState,
																this.p1ControllerMapping.getHorizontalAxisRightStick(),
																this.p1ControllerMapping.getVerticalAxisRightStick(),
																previousRsDirection);
		this.p1ControllerState.setRightPadAxisDirection(rsDirection);

		moveP1BasedOnCurrentAndPreviousAxisDirection(rsDirection, previousRsDirection);
	}

	protected static AxisDirection determineNewDirection(final float[] axisState, final int horizontalAxisId,
														 final int verticalAxisId, final AxisDirection previousDirection) {
		final int axisArraySize = axisState.length;

		AxisDirection horizontalDirection = AxisDirection.NONE;
		AxisDirection verticalDirection = AxisDirection.NONE;
		if (axisArraySize > horizontalAxisId) {
			horizontalDirection = getHorizontalAxisDirection(axisState[horizontalAxisId],
															 DEFAULT_CONTROLLER_AXIS_DEAD_ZONE);
		}

		if (axisArraySize > verticalAxisId) {
			verticalDirection = getVerticalAxisDirection(axisState[verticalAxisId], DEFAULT_CONTROLLER_AXIS_DEAD_ZONE);
		}

		final AxisDirection newDirection = determineAxisDirection(horizontalDirection, verticalDirection,
																  previousDirection);
		LOG.debug("Previous Direction: {}, new direction {}", previousDirection, newDirection);


		return newDirection;
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

	protected static boolean isDirectionalPadButton(final int buttonId, final ControllerMapping controllerMapping)
	{
		return buttonId == controllerMapping.getLeft() || buttonId == controllerMapping.getRight()
				|| buttonId == controllerMapping.getUp() || buttonId == controllerMapping.getDown();
	}

	protected static boolean checkDpadChangeRealistic(final boolean isLastDpadMoveAttemptSuccessful,
											   final long lastDpadChangeNanoTime, final String directionNameForDebug) {
		final long now = System.nanoTime();
		final long difference = now - lastDpadChangeNanoTime;
		if (isLastDpadMoveAttemptSuccessful && difference < MIN_DPAD_TIME_DIFFERENCE) {
			LOG.debug(
					"DENIED to move {} but the time difference ({}ms) between dpad changes was too small, " + "and successful attempt already made recently.",
					directionNameForDebug, difference / 1_000_000);
			return false;
		} else {
			LOG.debug("ALLOWED to move {} the time difference ({}ms) between dpad changes was large enough.",
					  directionNameForDebug, difference / 1_000_000);
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
