package lv.herbis.cnakes.menus;

public interface MenuItem {

	String getName();

	boolean enter();

	boolean back();

	boolean up();

	boolean down();

	boolean left();

	boolean right();

}
