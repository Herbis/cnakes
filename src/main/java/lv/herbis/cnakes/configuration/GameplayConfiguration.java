package lv.herbis.cnakes.configuration;

public class GameplayConfiguration {
	private boolean brightenMovementLine = false;
	private int gameSpeed = 40;

	public boolean isBrightenMovementLine() {
		return brightenMovementLine;
	}

	public void setBrightenMovementLine(final boolean brightenMovementLine) {
		this.brightenMovementLine = brightenMovementLine;
	}

	public int getGameSpeed() {
		return gameSpeed;
	}

	public void setGameSpeed(int gameSpeed) {
		this.gameSpeed = gameSpeed;
	}
}
