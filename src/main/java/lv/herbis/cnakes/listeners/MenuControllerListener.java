package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;

public class MenuControllerListener extends ControllerListener {

	private static final Logger LOG = LogManager.getLogger(MenuControllerListener.class);

	private final MenuNavigation menuNavigation;

	public MenuControllerListener(final MenuNavigation menuNavigation) {
		this.menuNavigation = menuNavigation;
		initGamePads();
	}

	@Override
	public void invoke(final int i, final int i1) {
		// This is for handling controller connects / disconnects if it's ever going to be implemented.
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

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		final MenuControllerListener that = (MenuControllerListener) o;
		return Objects.equals(this.menuNavigation, that.menuNavigation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.menuNavigation);
	}
}
