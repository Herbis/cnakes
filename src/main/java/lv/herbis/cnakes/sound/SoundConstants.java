package lv.herbis.cnakes.sound;

public class SoundConstants {

	private SoundConstants() {
		// Static access only
	}


	public static class MenuSounds {
		private MenuSounds() {
			// Static access only
		}

		public static final String NAV_UP_DOWN_SOURCE = "menu_up_down";
		public static final String NAV_UP_DOWN_PATH = "sounds/menu_move.ogg";
	}

	public static class GameplaySounds {
		public static final String COLLECT_BUG_SOURCE = "collect_bug";
		public static final String COLLECT_BUG_PATH = "sounds/collect_bug.ogg";

		public static final String BAD_ACTION_SOURCE = "bad_action";
		public static final String BAD_ACTION_PATH = "sounds/bad_action.ogg";

	}
}
