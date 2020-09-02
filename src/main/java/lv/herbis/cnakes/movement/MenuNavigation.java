package lv.herbis.cnakes.movement;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.menus.ExitGameMenuItem;
import lv.herbis.cnakes.menus.HighScoreGameMenuItem;
import lv.herbis.cnakes.menus.MenuItem;
import lv.herbis.cnakes.menus.StartGameMenuItem;
import lv.herbis.cnakes.constants.SoundConstants;
import lv.herbis.cnakes.sound.SoundManager;


public class MenuNavigation {

	private final SoundManager soundManager;
	private final MenuItem[] menuItems;

	private MenuItem activeItem;


	private Object pendingItem;

	public MenuNavigation(final CnakesConfiguration configuration, final long windowId,
						  final SoundManager soundManager) {
		this.soundManager = soundManager;
		final MenuItem startGameMenuItem = new StartGameMenuItem(configuration, this, windowId, this.soundManager);
		final MenuItem highScoresMenuItem = new HighScoreGameMenuItem(this, windowId);
		final MenuItem exitGameMenuItem = new ExitGameMenuItem(windowId);
		this.activeItem = startGameMenuItem;
		this.menuItems = new MenuItem[]{startGameMenuItem, highScoresMenuItem, exitGameMenuItem};

		initSounds();
	}

	private void initSounds() {
		this.soundManager
				.createSound(SoundConstants.MenuSounds.NAV_UP_DOWN_SOURCE, SoundConstants.MenuSounds.NAV_UP_DOWN_PATH);
		this.soundManager
				.createSound(SoundConstants.MenuSounds.NAV_ENTER_SOURCE, SoundConstants.MenuSounds.NAV_ENTER_PATH);
	}

	public void enterSelectedItem() {
		if (this.activeItem.enter()) {
			this.soundManager.playSoundSource(SoundConstants.MenuSounds.NAV_ENTER_SOURCE);
		}
	}

	public void moveDown() {
		MenuItem newActiveMenuItem = this.menuItems[0]; // Default to first menu item

		for (int i = 0; i < this.menuItems.length - 1; i++) {  // Ignore last, because it's covered by default case.
			final MenuItem item = this.menuItems[i];
			if (item == this.activeItem) {
				newActiveMenuItem = this.menuItems[i + 1];
				break;
			}
		}

		setActiveItem(newActiveMenuItem);
		this.soundManager.playSoundSource(SoundConstants.MenuSounds.NAV_UP_DOWN_SOURCE);
	}

	public void moveUp() {
		MenuItem newActiveMenuItem = this.menuItems[this.menuItems.length - 1]; // Default to last menu item

		for (int i = 1; i < this.menuItems.length; i++) { // Ignore first, because it's covered by default case.
			final MenuItem item = this.menuItems[i];
			if (item == this.activeItem) {
				newActiveMenuItem = this.menuItems[i - 1];
				break;
			}
		}

		setActiveItem(newActiveMenuItem);
		this.soundManager.playSoundSource(SoundConstants.MenuSounds.NAV_UP_DOWN_SOURCE);
	}


	public void moveRight() {
		getActiveItem().right();
	}

	public void moveLeft() {
		getActiveItem().left();
	}

	public MenuItem[] getMenuItems() {
		// Sub menu items?
		return this.menuItems;
	}

	public Object usePendingItem() {
		final Object pendingItemTmp = this.pendingItem;
		this.pendingItem = null;

		return pendingItemTmp;
	}

	public void setPendingItem(final Object pendingItem) {
		this.pendingItem = pendingItem;
	}

	public MenuItem getActiveItem() {
		return this.activeItem;
	}

	public void setActiveItem(final MenuItem activeItem) {
		this.activeItem = activeItem;
	}

}
