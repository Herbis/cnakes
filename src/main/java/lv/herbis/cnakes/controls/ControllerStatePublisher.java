package lv.herbis.cnakes.controls;

import lv.herbis.cnakes.listeners.ControllerListener;
import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class ControllerStatePublisher implements Runnable {
	private static final Logger LOG = LogManager.getLogger(ControllerStatePublisher.class);
	private static final long UPDATE_INTERVAL_NS = 10_000_000; // 1_000_000 NANO SECONDS = 1 MILLI SECOND
	private static final byte PRESSED_BUTTON_VALUE = 1;
	protected static final float AXIS_MIN_VALUE_CHANGE = 0.45f;

	private static ControllerListener controllerListener;
	private static MenuNavigation menuNavigation;
	private static boolean stop;

	private long lastCheckNano;
	final Map<Integer, byte[]> previousControllerButtonStates = new HashMap<>();
	final Map<Integer, float[]> previousControllerAxisStates = new HashMap<>();

	@Override
	public void run() {
		LOG.debug("Running Controller State Publisher");
		while (!stop) {
			final ControllerListener currentListener = ControllerStatePublisher.controllerListener;

			if (currentListener == null) {
				LOG.debug("Current Listener is null");

				continue;
			}

			final long currentTime = System.nanoTime();
			if (currentTime - this.lastCheckNano > UPDATE_INTERVAL_NS) {
				this.lastCheckNano = currentTime;
				try {
					checkState(currentListener, GLFW_JOYSTICK_1);
				} catch (final MainMenu.ReturnToMenuRequest e) {
					getMenuNavigation().setPendingItem(e);
				}
			}
		}
	}


	public void checkState(final ControllerListener currentListener, final int controllerId) {

		final ByteBuffer buttonStatus = glfwGetJoystickButtons(controllerId);

		if (buttonStatus == null || buttonStatus.capacity() == 0) {
			LOG.trace("Button status is null or capacity is empty for joystick {}.", controllerId);
			return;
		}

		final byte[] buttonStatusArray = new byte[buttonStatus.capacity()];
		for (int i = 0; i < buttonStatus.capacity(); i++) {
			final byte buttonValue = buttonStatus.get(i);
			buttonStatusArray[i] = buttonValue;
			invokeButtonStateChangeIfItHasOccurred(controllerId, i, buttonValue, currentListener);
		}

		this.previousControllerButtonStates.put(controllerId, buttonStatusArray);

		/* Code for getting Controller Axis */
		final FloatBuffer axisStatus = glfwGetJoystickAxes(controllerId);

		if (axisStatus != null) {
			final float[] axisStatusArray = new float[axisStatus.capacity()];
			boolean anyAxisChanged = false;
			for (int i = 0; i < axisStatus.capacity(); i++) {
				final float axisValue = axisStatus.get(i);
				axisStatusArray[i] = axisStatus.get(i);

				anyAxisChanged = anyAxisChanged || isAxisValueDifferentEnough(i, axisValue,
																			  this.previousControllerAxisStates
																					  .get(controllerId));
			}

			if (anyAxisChanged) {
				LOG.debug("Axis has changed!");

				currentListener.invokeAxisStateChange(controllerId, axisStatusArray);
				this.previousControllerAxisStates.put(controllerId, axisStatusArray);
			} else {
				this.previousControllerAxisStates.putIfAbsent(controllerId, axisStatusArray);
			}
		}
	}

	private void invokeButtonStateChangeIfItHasOccurred(final int controllerId, final int buttonId, final byte value,
														final ControllerListener listener) {
		final byte[] previousButtonState = this.previousControllerButtonStates.get(controllerId);
		if (previousButtonState != null && previousButtonState.length > buttonId && previousButtonState[buttonId] != value) {
			final ButtonState state = (value == PRESSED_BUTTON_VALUE) ? ButtonState.PRESSED : ButtonState.RELEASED;
			listener.invokeButtonStateChange(controllerId, buttonId, state);
		}
	}

	protected static boolean isAxisValueDifferentEnough(final int axisId, final float value,
														final float[] previousAxisState) {
		return previousAxisState != null && previousAxisState.length > axisId && previousAxisState[axisId] != value && Math
				.abs(previousAxisState[axisId] - value) > AXIS_MIN_VALUE_CHANGE;
	}

	public static void stop() {
		stop = true;
	}

	public static void setGamePadListener(final ControllerListener controllerListener) {
		ControllerStatePublisher.controllerListener = controllerListener;
	}

	public static MenuNavigation getMenuNavigation() {
		return menuNavigation;
	}

	public static void setMenuNavigation(final MenuNavigation menuNavigation) {
		ControllerStatePublisher.menuNavigation = menuNavigation;
	}
}
