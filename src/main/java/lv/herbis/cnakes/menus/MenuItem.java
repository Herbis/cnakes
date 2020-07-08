package lv.herbis.cnakes.menus;

import other.fontloader.Color4f;

public interface MenuItem {

	abstract Color4f getColor();

	abstract String getName();

	default boolean enter() {
		return false;
	}

	default boolean back() {
		return false;
	}

	default boolean up() {
		return false;
	}

	default boolean down() {
		return false;
	}

	default boolean left() {
		return false;
	}

	default boolean right() {
		return false;
	}

}
