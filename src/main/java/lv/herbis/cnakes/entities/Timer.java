package lv.herbis.cnakes.entities;

public class Timer {
	
	boolean stopped = true;
	
	long startTime = 0; // nanoseconds from which to start counting. 
	long goalTime = 0; // how many nanoseconds should we count to/from.
	long pauseTime = 0; 
	long timeLeftAtPause = 0; 
	long totalPausedTime = 0;
	
	
	/**
	 * Pass time in miliseconds
	 * @param goalTime
	 */
	public Timer(long goalTime_ms) {
		this.goalTime = goalTime_ms * 1000000;
	}
	
	public static long minutesToMiliseconds(long minutes) {
		return minutes * 60000;
	}
	
	public void start() {
		stopped = false; 
		startTime = System.nanoTime();
	}
	
	public long getTimeLeft() {
		if(stopped) {
			return timeLeftAtPause;
		} else {
			long timePassed = System.nanoTime() - (startTime + totalPausedTime);
			long timeLeft = goalTime - timePassed;
			return timeLeft < 0 ? 0 : timeLeft / 1000000 ; // if time left is less than 0, return 0;
		}
	}
	
	public void pause() {
		if(stopped) {
			totalPausedTime += System.nanoTime() - pauseTime; // time we spent in pause mode. 
			pauseTime = 0;
			stopped = false;
			//TODO calculate time passed
		} else {
			/* Record the time pause was pressed. */
			pauseTime = System.nanoTime();
			timeLeftAtPause = getTimeLeft();
			stopped = true;
		}
	}
	
	public void reset() {
		stopped = true; 
		totalPausedTime = 0;
		startTime = 0;
	}
}
