package lv.herbis.cnakes.menus;

public class StartGameMenuItem implements MenuItem {

	private static final String NAME = "START";

	@Override
	public String getName() {
		return NAME;
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
