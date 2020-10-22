package lv.herbis.cnakes.movement;

import lv.herbis.cnakes.constants.SoundConstants;
import lv.herbis.cnakes.context.ContextItems;
import lv.herbis.cnakes.menus.ExitGameMenuItem;
import lv.herbis.cnakes.menus.HighScoreGameMenuItem;
import lv.herbis.cnakes.menus.MenuItem;
import lv.herbis.cnakes.menus.StartGameMenuItem;
import lv.herbis.cnakes.sound.SoundManager;

public class DefaultMenuNavigation implements MenuNavigation {
	private final SoundManager soundManager;
	private final MenuItem[] menuItems;

	private MenuItem activeItem;
	private Object pendingItem;

	public DefaultMenuNavigation(final ContextItems contextItems) {
		this.soundManager = contextItems.getSoundManager();
		final MenuItem startGameMenuItem = new StartGameMenuItem(contextItems);
		final MenuItem highScoresMenuItem = new HighScoreGameMenuItem(contextItems);
		final MenuItem exitGameMenuItem = new ExitGameMenuItem(contextItems);
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

	@Override
	public void enterSelectedItem() {
		if (this.getActiveItem().enter()) {
			this.soundManager.playSoundSource(SoundConstants.MenuSounds.NAV_ENTER_SOURCE);
		}
	}

	@Override
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

	@Override
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


	@Override
	public void moveRight() {
		getActiveItem().right();
	}

	@Override
	public void moveLeft() {
		getActiveItem().left();
	}

	@Override
	public MenuItem[] getMenuItems() {
		// Sub menu items?
		return this.menuItems;
	}

	@Override
	public Object usePendingItem() {
		final Object pendingItemTmp = this.pendingItem;
		this.pendingItem = null;

		return pendingItemTmp;
	}

	@Override
	public void setPendingItem(final Object pendingItem) {
		this.pendingItem = pendingItem;
	}

	@Override
	public MenuItem getActiveItem() {
		return this.activeItem;
	}

	@Override
	public void setActiveItem(final MenuItem activeItem) {
		this.activeItem = activeItem;
	}
}
