package lv.herbis.cnakes.configuration;

public class VideoConfiguration {

    private ResolutionConfiguration resolution;
    private int scale;
    private Integer monitor;

    public ResolutionConfiguration getResolution() {
        return resolution;
    }

    public void setResolution(final ResolutionConfiguration resolution) {
        this.resolution = resolution;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(final int scale) {
        this.scale = scale;
    }

    public Integer getMonitor() {
        return monitor;
    }

    public void setMonitor(final Integer monitor) {
        this.monitor = monitor;
    }
}
