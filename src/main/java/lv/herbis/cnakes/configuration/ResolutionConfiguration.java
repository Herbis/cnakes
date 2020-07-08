package lv.herbis.cnakes.configuration;

public class ResolutionConfiguration {
	private int horizontal = 1280;
	private int vertical = 720;
	private boolean fullScreen = true;
	private boolean autoConfig = true;

	public int getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(final int horizontal) {
		this.horizontal = horizontal;
	}

	public int getVertical() {
		return vertical;
	}

	public void setVertical(final int vertical) {
		this.vertical = vertical;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(final boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public boolean isAutoConfig() {
		return autoConfig;
	}

	public void setAutoConfig(boolean autoConfig) {
		this.autoConfig = autoConfig;
	}
}
