package lv.herbis.cnakes.menus;
import lv.herbis.cnakes.levels.singleplayer.LevelScreen;


public class StartScreen {
	
	public static void main(String[] args) {
		
		
		LevelScreen ls = new LevelScreen();
		Thread lsThread = new Thread(ls);		
		lsThread.start();
	}
}
