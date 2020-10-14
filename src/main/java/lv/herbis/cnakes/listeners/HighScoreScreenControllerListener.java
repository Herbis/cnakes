package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.entities.Pagination;
import lv.herbis.cnakes.menus.MainMenu;

public class HighScoreScreenControllerListener extends ControllerListener {

	private final Pagination pagination;

	public HighScoreScreenControllerListener(final Pagination pagination) {
		this.pagination = pagination;
		initGamePads();
	}

	@Override
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
	protected void moveP1BasedOnCurrentAndPreviousAxisDirection(final AxisDirection direction,
																final AxisDirection previousDirection) {
		if (!previousDirection.equals(direction)) {
			 if (AxisDirection.LEFT.equals(direction)) {
				this.pagination.previousPage();
			} else if (AxisDirection.RIGHT.equals(direction)) {
				this.pagination.nextPage();
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
