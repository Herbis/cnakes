package lv.herbis.cnakes.status;

public interface GameStatus {

	void start();

	void pause();

	boolean isPaused();

	boolean isPlayed();

	boolean hasJustStarted();

	void end();

	boolean hasEnded();

	void reset();

}
