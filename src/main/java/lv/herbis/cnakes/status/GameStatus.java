package lv.herbis.cnakes.status;

public interface GameStatus {

	void start();

	void pause();

	boolean isPaused();

	boolean isBeingPlayed();

	boolean hasJustStarted();

	void end();

	boolean hasEnded();

	void reset();

}
