package lv.herbis.cnakes.constants;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CnakesConstants {

	public static final Path SAVE_FILE_PATH = Paths
			.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath(), "My Games", "cnakes");
	public static final String HIGH_SCORE_FILE = "classic.hs";
	public static final String APP_ICON_PATH = "icons/icon.png";

	public static final String LOG_STACKTRACE = "Stacktrace: ";

	private CnakesConstants() {
		// Only static access
	}

}
