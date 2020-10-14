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



		final AxisDirection previousLsDirection = this.p1ControllerState.getLeftPadAxisDirection();
		final AxisDirection lsDirection = determineNewDirection(axisState,
																this.p1ControllerMapping.getHorizontalAxisLeftStick(),
																this.p1ControllerMapping.getVerticalAxisLeftStick(),
																previousLsDirection);
		this.p1ControllerState.setLeftPadAxisDirection(lsDirection);

		navigateBasedOnCurrentAndPreviousAxisDirection(lsDirection, previousLsDirection);


		final AxisDirection previousRsDirection = this.p1ControllerState.getLeftPadAxisDirection();
		final AxisDirection rsDirection = determineNewDirection(axisState,
																this.p1ControllerMapping.getHorizontalAxisRightStick(),
																this.p1ControllerMapping.getVerticalAxisRightStick(),
																previousRsDirection);
		this.p1ControllerState.setRightPadAxisDirection(rsDirection);

		navigateBasedOnCurrentAndPreviousAxisDirection(rsDirection, previousRsDirection);
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

	protected void navigateBasedOnCurrentAndPreviousAxisDirection(final AxisDirection direction,
																  final AxisDirection previousDirection) {
		if (!previousDirection.equals(direction)) {
			if (AxisDirection.DOWN.equals(direction)) {
				this.menuNavigation.moveDown();
			} else if (AxisDirection.UP.equals(direction)) {
				this.menuNavigation.moveUp();
			} else if (AxisDirection.LEFT.equals(direction)) {
				this.menuNavigation.moveLeft();
			} else if (AxisDirection.RIGHT.equals(direction)) {
				this.menuNavigation.moveRight();
			}
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
