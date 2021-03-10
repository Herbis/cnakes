package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.screens.singleplayer.SinglePlayerLevelScreen;
import lv.herbis.cnakes.status.GameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWCharCallback;

public class NameInputCharListener extends GLFWCharCallback {
	private static final Logger LOG = LogManager.getLogger(SinglePlayerLevelScreen.class);

	private final GameStatus gameStatus;

	public NameInputCharListener(final GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	@Override
	public void invoke(long windowId, int codepoint) {
		final char keyChar = (char) codepoint;
		if (this.gameStatus.isValidHighScoreCharacter(keyChar)) {
			gameStatus.setHighScoreName(gameStatus.getHighScoreName() + keyChar);
		}
	}
}
