package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.status.GameStatus;
import org.lwjgl.glfw.GLFWCharCallback;

import java.util.Objects;

public class NameInputCharListener extends GLFWCharCallback {

	private final GameStatus gameStatus;

	public NameInputCharListener(final GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	@Override
	public void invoke(final long windowId, final int codepoint) {
		final char keyChar = (char) codepoint;
		if (this.gameStatus.isValidHighScoreCharacter(keyChar)) {
			this.gameStatus.setHighScoreName(this.gameStatus.getHighScoreName() + keyChar);
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		final NameInputCharListener that = (NameInputCharListener) o;
		return Objects.equals(this.gameStatus, that.gameStatus);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.gameStatus);
	}
}
