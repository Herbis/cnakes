package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.ButtonState;
import lv.herbis.cnakes.movement.MenuNavigation;

public class MenuControllerListener extends ControllerListener {

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
	public boolean equals(final Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
