package lv.herbis.cnakes.configuration;

public class CnakesConfiguration {
	private VideoConfiguration video;
	private GameplayConfiguration gameplay;

	private boolean defaultConfig = true;
	private String userName = "Player 1";

	public VideoConfiguration getVideo() {
		if (this.video == null) {
			this.video = new VideoConfiguration();
		}
		return this.video;
	}

	public void setVideo(final VideoConfiguration video) {
		this.video = video;
	}

	public GameplayConfiguration getGameplay() {
		if (this.gameplay == null) {
			this.gameplay = new GameplayConfiguration();
		}
		return this.gameplay;
	}

	public void setGameplay(final GameplayConfiguration gameplay) {
		this.gameplay = gameplay;
	}

	public boolean isDefaultConfig() {
		return defaultConfig;
	}

	public void setDefaultConfig(boolean defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
