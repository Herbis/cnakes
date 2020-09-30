package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import org.lwjgl.glfw.GLFWJoystickCallback;

public abstract class GamePadListener extends GLFWJoystickCallback {

	public abstract void invokeButtonStateChange(int gamePadId, int buttonId, ButtonState state);

}
