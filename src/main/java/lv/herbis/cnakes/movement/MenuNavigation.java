package lv.herbis.cnakes.movement;

import lv.herbis.cnakes.menus.MenuItem;
import lv.herbis.cnakes.menus.StartGameMenuItem;


public class MenuNavigation {

	private MenuItem activeItem;

	private MenuItem[] menuItems;

	public MenuNavigation() {
		final MenuItem startGameMenuItem = new StartGameMenuItem();
		activeItem = startGameMenuItem;
		menuItems = new MenuItem[]{startGameMenuItem};
	}

	public MenuItem[] getMenuItems() {
		// Sub menu items?
		return menuItems;
	}


}
