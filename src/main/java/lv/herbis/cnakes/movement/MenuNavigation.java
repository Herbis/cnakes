package lv.herbis.cnakes.movement;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.menus.ExitGameMenuItem;
import lv.herbis.cnakes.menus.MenuItem;
import lv.herbis.cnakes.menus.StartGameMenuItem;


public class MenuNavigation {

	private MenuItem activeItem;

	private MenuItem[] menuItems;

	private Object pendingItem;

	public MenuNavigation(final CnakesConfiguration configuration, final long windowId) {
		final MenuItem startGameMenuItem = new StartGameMenuItem(configuration, this, windowId);
		final MenuItem exitGameMenuItem = new ExitGameMenuItem();
		activeItem = startGameMenuItem;
		menuItems = new MenuItem[]{startGameMenuItem, exitGameMenuItem};
	}

	public MenuItem[] getMenuItems() {
		// Sub menu items?
		return menuItems;
	}

	public MenuItem getActiveItem() {
		return activeItem;
	}

	public void setActiveItem(MenuItem activeItem) {
		this.activeItem = activeItem;
	}

	public void moveDown() {
		MenuItem newActiveMenuItem = menuItems[0]; // Default to first menu item

		for (int i = 0; i < menuItems.length - 1; i++) {  // Ignore last, because it's covered by default case.
			final MenuItem item = menuItems[i];
			if (item == activeItem ) {
				newActiveMenuItem = menuItems[i + 1];
				break;
			}
		}

		setActiveItem(newActiveMenuItem);
	}

	public void moveUp() {
		MenuItem newActiveMenuItem = menuItems[menuItems.length - 1]; // Default to last menu item

		for (int i = 1; i < menuItems.length; i++) { // Ignore first, because it's covered by default case.
			final MenuItem item = menuItems[i];
			if (item == activeItem ) {
				newActiveMenuItem = menuItems[i - 1];
				break;
			}
		}

		setActiveItem(newActiveMenuItem);
	}

	public Object usePendingItem() {
		final Object pendingItemTmp = this.pendingItem;
		this.pendingItem = null;

		return pendingItemTmp;
	}

	public void setPendingItem(final Object pendingItem) {
		this.pendingItem = pendingItem;
	}
}
