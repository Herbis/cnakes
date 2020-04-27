package lv.herbis.cnakes.configuration;

public class ResolutionConfiguration {
    private int horizontal;
    private int vertical;
    private boolean fullScreen;

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
}
