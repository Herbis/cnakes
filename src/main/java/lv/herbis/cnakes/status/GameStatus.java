package lv.herbis.cnakes.status;

public interface GameStatus {
	
	public abstract void start();
	
	public abstract void pause();
	
	public abstract boolean isPaused();
	
	public abstract boolean isPlayed();
	
	public abstract boolean hasJustStarted();
	
	public abstract void end();
	
	public abstract boolean hasEnded();
	
	public abstract void reset();
	
}
