package lv.herbis.cnakes.constants;

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

		public static final String NAV_ENTER_SOURCE = "menu_enter";
		public static final String NAV_ENTER_PATH = "sounds/menu_select.ogg";
	}

	public static class GameplaySounds {

		private GameplaySounds() {
			// Static access only
		}

		public static final String COLLECT_BUG_SOURCE = "collect_bug";
		public static final String COLLECT_BUG_PATH = "sounds/collect_bug.ogg";

		public static final String BAD_ACTION_SOURCE = "bad_action";
		public static final String BAD_ACTION_PATH = "sounds/bad_action.ogg";

		public static final String EAT_TAIL_SOURCE = "eat_tail";
		public static final String EAT_TAIL_PATH = "sounds/eat_tail.ogg";

	}
}
