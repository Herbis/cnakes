package lv.herbis.cnakes.menus;

public class ExitGameMenuItem implements MenuItem {

	final String name = "EXIT";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean enter() {
		return false;
	}

	@Override
	public boolean back() {
		return false;
	}

	@Override
	public boolean up() {
		return false;
	}

	@Override
	public boolean down() {
		return false;
	}

	@Override
	public boolean left() {
		return false;
	}

	@Override
	public boolean right() {
		return false;
	}
}
