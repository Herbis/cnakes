package lv.herbis.cnakes.controls;

import lv.herbis.cnakes.listeners.ControllerListener;
import lv.herbis.cnakes.menus.MainMenu;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickButtons;

public class ControllerStatePublisher implements Runnable {
	private static final Logger LOG = LogManager.getLogger(ControllerStatePublisher.class);
	private static final long UPDATE_INTERVAL_NS = 10_000_000; // NANO SECONDS
	private static final byte PRESSED_BUTTON_VALUE = 1;

	private static ControllerListener controllerListener;
	private static MenuNavigation menuNavigation;
	private static boolean stop;

	private long lastCheckNano;
	private byte[] previousButtonState;

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
					checkState(currentListener);
				} catch (final MainMenu.ReturnToMenuRequest e) {
					getMenuNavigation().setPendingItem(e);
				}
			}
		}
	}


	public void checkState(final ControllerListener currentListener) {

		final ByteBuffer buttonStatus = glfwGetJoystickButtons(GLFW_JOYSTICK_1);

		if (buttonStatus == null || buttonStatus.capacity() == 0) {
			LOG.trace("Button status is null or capacity is empty for joystick {}.", GLFW_JOYSTICK_1);
			return;
		}

		final byte[] buttonStatusArray = new byte[buttonStatus.capacity()];
		for (int i = 0; i < buttonStatus.capacity(); i++) {
			final byte buttonValue = buttonStatus.get(i);
			buttonStatusArray[i] = buttonValue;
			if (this.previousButtonState != null && this.previousButtonState.length > i) {
				invokeButtonStateChangeIfItHasOccurred(GLFW_JOYSTICK_1, i, buttonValue, currentListener);
			}

		}

		this.previousButtonState = buttonStatusArray;

		/* Code for getting Controller Axis

		FloatBuffer axisStatus = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
		if (axisStatus != null)
		{
			for (int i = 0; i < axisStatus.capacity(); i++)
			{
				stringBuilder.append("Axis(").append(i).append("):").append(axisStatus.get(i)).append(",");
			}
		}*/
	}

	private void invokeButtonStateChangeIfItHasOccurred(final int gamePadId, final int buttonId, final byte value,
														final ControllerListener listener) {
		if (this.previousButtonState != null && this.previousButtonState.length > buttonId && this.previousButtonState[buttonId] != value) {

			final ButtonState state = (value == PRESSED_BUTTON_VALUE) ? ButtonState.PRESSED : ButtonState.RELEASED;
			listener.invokeButtonStateChange(gamePadId, buttonId, state);
		}
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
