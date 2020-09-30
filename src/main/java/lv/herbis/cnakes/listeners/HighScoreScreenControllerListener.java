package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.entities.Pagination;
import lv.herbis.cnakes.menus.MainMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;

public class HighScoreScreenControllerListener extends ControllerListener {

	private static final Logger LOG = LogManager.getLogger(HighScoreScreenControllerListener.class);

	private final Pagination pagination;

	public HighScoreScreenControllerListener(final Pagination pagination) {
		this.pagination = pagination;
		initGamePads();
	}

	@Override
	public void invoke(final int i, final int i1) {
		// This is for handling controller connects / disconnects if it's ever going to be implemented.
	}

	@Override
	public void invokeButtonStateChange(final int controllerId, final int buttonId, final ButtonState state) {
		LOG.debug("Button state change invoked controllerId: {}, buttonId: {}, state: {}.", controllerId, buttonId,
				  state);

		if (GLFW_JOYSTICK_1 == controllerId) {
			processP1ControllerStateChange(buttonId, state);
		}
	}

	public void processP1ControllerStateChange(final int buttonId, final ButtonState state) {
		if (this.p1ControllerMapping == null) {
			return;
		}

		if (ButtonState.PRESSED.equals(state)) {
			if (this.p1ControllerMapping.getLeft() == buttonId) {
				this.pagination.previousPage();
			} else if (this.p1ControllerMapping.getRight() == buttonId) {
				this.pagination.nextPage();
			} else if (this.p1ControllerMapping.getBack() == buttonId || this.p1ControllerMapping
					.getCancel() == buttonId) {
				throw new MainMenu.ReturnToMenuRequest();
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
		final HighScoreScreenControllerListener that = (HighScoreScreenControllerListener) o;
		return Objects.equals(this.pagination, that.pagination) && Objects
				.equals(this.p1ControllerMapping, that.p1ControllerMapping);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.pagination, this.p1ControllerMapping);
	}
}
