package lv.herbis.cnakes.movement;

import lv.herbis.cnakes.menus.MenuItem;


public interface MenuNavigation {
	void enterSelectedItem();

	void moveDown();

	void moveUp();

	void moveRight();

	void moveLeft();

	MenuItem[] getMenuItems();

	Object usePendingItem();

	void setPendingItem(final Object pendingItem);

	MenuItem getActiveItem();

	void setActiveItem(final MenuItem activeItem);
}
