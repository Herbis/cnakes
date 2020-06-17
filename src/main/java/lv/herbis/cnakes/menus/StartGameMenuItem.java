package lv.herbis.cnakes.menus;

public class StartGameMenuItem implements MenuItem {

	private String name = "START";

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
