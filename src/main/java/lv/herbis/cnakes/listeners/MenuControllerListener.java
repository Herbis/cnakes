package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuControllerListener extends ControllerListener {
	private static final Logger LOG = LogManager.getLogger(MenuControllerListener.class);

	private final MenuNavigation menuNavigation;


	public MenuControllerListener(final MenuNavigation menuNavigation) {
		this.menuNavigation = menuNavigation;
		initGamePads();
	}

	@Override
	public void processP1ControllerStateChange(final int buttonId, final ButtonState state) {
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
	public void processP1ControllerAxisStateChange(final float[] axisState) {
		final int axisArraySize = axisState.length;
		final AxisDirection lsPreviousDirection = p1ControllerState.getLeftPadAxisDirection();

		AxisDirection lsHorizontalDirection = AxisDirection.NONE;
		AxisDirection lsVerticalDirection = AxisDirection.NONE;
		if (axisArraySize > this.p1ControllerMapping.getHorizontalAxisLeftStick()) {
			lsHorizontalDirection = getHorizontalAxisDirection(axisState[this.p1ControllerMapping.getHorizontalAxisLeftStick()], DEFAULT_CONTROLLER_AXIS_DEAD_ZONE);
		}

		if (axisArraySize > this.p1ControllerMapping.getVerticalAxisLeftStick()) {
			lsVerticalDirection = getVerticalAxisDirection(axisState[this.p1ControllerMapping.getVerticalAxisLeftStick()], DEFAULT_CONTROLLER_AXIS_DEAD_ZONE);
		}

		final AxisDirection newDirection = determineAxisDirection(lsHorizontalDirection, lsVerticalDirection, lsPreviousDirection);
		LOG.debug("Previous Direction: {}, new direction {}", lsPreviousDirection, newDirection);
		if (!lsPreviousDirection.equals(newDirection))
		{
			if (AxisDirection.DOWN.equals(newDirection)) {
				this.menuNavigation.moveDown();
			} else if (AxisDirection.UP.equals(newDirection)) {
				this.menuNavigation.moveUp();
			} else if (AxisDirection.LEFT.equals(newDirection)) {
				this.menuNavigation.moveLeft();
			} else if (AxisDirection.RIGHT.equals(newDirection)) {
				this.menuNavigation.moveRight();
			}
		}

		this.p1ControllerState.setLeftPadAxisDirection(newDirection);
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
