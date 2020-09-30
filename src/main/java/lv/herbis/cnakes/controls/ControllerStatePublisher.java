package lv.herbis.cnakes.controls;

import lv.herbis.cnakes.listeners.GamePadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickButtons;

public class ControllerStatePublisher implements Runnable {
	private static final Logger LOG = LogManager.getLogger(ControllerStatePublisher.class);
	private static final long UPDATE_INTERVAL_NS = 10_000_000; // NANO SECONDS
	private static final byte PRESSED_BUTTON_VALUE = 1;

	private static GamePadListener gamePadListener;
	private static boolean stop;

	private long lastCheckNano;
	private byte[] previousButtonState;

	public ControllerStatePublisher() {
	}

	@Override
	public void run() {
		LOG.debug("Runing Controller State Publisher");
		while (!stop) {
			final GamePadListener currentListener = ControllerStatePublisher.gamePadListener;

			if (currentListener == null) {
				LOG.debug("Current Listener is null");

				continue;
			}

			final long currentTime = System.nanoTime();
			if (currentTime - this.lastCheckNano > UPDATE_INTERVAL_NS) {
				this.lastCheckNano = currentTime;
				checkState(currentListener);
			}
		}
	}


	public void checkState(final GamePadListener currentListener) {

		final ByteBuffer buttonStatus = glfwGetJoystickButtons(GLFW_JOYSTICK_1);

		if (buttonStatus == null || buttonStatus.capacity() == 0) {
			LOG.trace("Button status is null or capacity is empty for joystick {}.", GLFW_JOYSTICK_1);
			return;
		}

		final byte[] buttonStatusArray = new byte[buttonStatus.capacity()];
		for (int i = 0; i < buttonStatus.capacity(); i++) {
			byte buttonValue = buttonStatus.get(i);
			buttonStatusArray[i] = buttonValue;
			if (this.previousButtonState != null && this.previousButtonState.length > i) {
				invokeButtonStateChangeIfItHasOccurred(GLFW_JOYSTICK_1, i, buttonValue, currentListener);
			}

		}

		this.previousButtonState = buttonStatusArray;

		/*FloatBuffer axisStatus = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
		if (axisStatus != null)
		{
			for (int i = 0; i < axisStatus.capacity(); i++)
			{
				stringBuilder.append("Axis(").append(i).append("):").append(axisStatus.get(i)).append(",");
			}
		}*/


		/*if (isButtonPressed(buttonStatus, this.p1ControllerMapping.getDown()) != this.p1ControllerState
				.isDownPressed()) {
			// trigger change
			if (this.p1ControllerState.isDownPressed()) {
				// trigger release event
			} else {
				// trigger press event
			}

			this.p1ControllerState.setDownPressed(!this.p1ControllerState.isDownPressed());
		}*/
	}

	private void invokeButtonStateChangeIfItHasOccurred(final int gamePadId, final int buttonId, final byte value,
														   final GamePadListener listener) {
		if (this.previousButtonState != null && this.previousButtonState.length > buttonId
				&& this.previousButtonState[buttonId] != value) {

			final ButtonState state = (value == PRESSED_BUTTON_VALUE) ? ButtonState.PRESSED : ButtonState.RELEASED;
			listener.invokeButtonStateChange(gamePadId, buttonId, state);
		}
	}

	public static void stop() {
		stop = true;
	}

	public static void setGamePadListener(final GamePadListener gamePadListener) {
		ControllerStatePublisher.gamePadListener = gamePadListener;
	}
}
