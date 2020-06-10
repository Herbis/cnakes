package lv.herbis.cnakes.constants;

import javax.swing.filechooser.FileSystemView;

public class CnakesConstants {

	public static final String SAVE_FILE_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\My Games\\cnakes\\";
	public static final String LOG_STACKTRACE = "Stacktrace: ";

	private CnakesConstants() {
		// Only static access
	}

}
