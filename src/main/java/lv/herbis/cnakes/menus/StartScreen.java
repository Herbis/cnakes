package lv.herbis.cnakes.menus;
import lv.herbis.cnakes.configuration.Configuration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import lv.herbis.cnakes.levels.singleplayer.LevelScreen;
import lv.herbis.cnakes.tools.ConfigurationUtil;


public class StartScreen {
	
	public static void main(String[] args) {
		try {
			loadGame(ConfigurationUtil.readConfiguration());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void loadGame(final Configuration configuration) {
		LevelScreen ls = new LevelScreen(configuration);
		Thread lsThread = new Thread(ls);
		lsThread.start();
	}
}
