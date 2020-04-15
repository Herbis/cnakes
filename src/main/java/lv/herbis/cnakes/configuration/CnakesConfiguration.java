package lv.herbis.cnakes.configuration;

public class CnakesConfiguration {
    private VideoConfiguration video;

    private GameplayConfiguration gameplay;

    public VideoConfiguration getVideo() {
        return video;
    }

    public void setVideo(VideoConfiguration video) {
        this.video = video;
    }

    public GameplayConfiguration getGameplay() {
        return gameplay;
    }

    public void setGameplay(GameplayConfiguration gameplay) {
        this.gameplay = gameplay;
    }
}
