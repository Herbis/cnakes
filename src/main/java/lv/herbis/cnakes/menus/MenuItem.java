package lv.herbis.cnakes.menus;

import java.awt.*;

public interface MenuItem {

	abstract Color getColor();

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
