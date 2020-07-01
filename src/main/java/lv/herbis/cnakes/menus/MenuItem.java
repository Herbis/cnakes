package lv.herbis.cnakes.menus;

import other.fontloader.Color4f;

public interface MenuItem {

	Color4f getColor();

	String getName();

	boolean enter();

	boolean back();

	boolean up();

	boolean down();

	boolean left();

	boolean right();

}
