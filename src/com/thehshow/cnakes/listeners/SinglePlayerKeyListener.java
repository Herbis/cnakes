package com.thehshow.cnakes.listeners;

import org.lwjgl.glfw.GLFWKeyCallback;

import com.thehshow.cnakes.staticaccess.MovingDirections;
import com.thehshow.cnakes.status.GameStatus;

import static org.lwjgl.glfw.GLFW.*;

public class SinglePlayerKeyListener extends GLFWKeyCallback {
	
	GameStatus GAME_STATUS;
	
	public SinglePlayerKeyListener(GameStatus gameStatus) {
		GAME_STATUS = gameStatus;
	}
	
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		
		if(action != GLFW_PRESS) {
			return;
		}
		
		if(GAME_STATUS.isPlayed() && !GAME_STATUS.hasEnded() && !GAME_STATUS.isPaused() ) {
			/* Actions allowed only when the game has not been started or has not ended or is not paused. */
			
			if(key == GLFW_KEY_LEFT) {	
	  			/* We can only start moving left, if we're not going right.*/ 
	  			if(MovingDirections.P1_DIRECTION != MovingDirections.RIGHT && MovingDirections.P1_DIRECTION != MovingDirections.LEFT) {
	  				MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.LEFT);
	  			}
	  			return;
	  		} else if(key == GLFW_KEY_RIGHT) {
	  			/* We can only start moving right, if we're not going left.*/ 
	  			if(MovingDirections.P1_DIRECTION != MovingDirections.LEFT && MovingDirections.P1_DIRECTION != MovingDirections.RIGHT) {
	  				MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.RIGHT);
	  			}
	  			return;
	  		} else if(key == GLFW_KEY_UP) {
	  			/* We can only start moving up, if we're not going down.*/ 
	  			if(MovingDirections.P1_DIRECTION != MovingDirections.DOWN && MovingDirections.P1_DIRECTION != MovingDirections.UP){
	  				MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.UP);
	  			}
	  			return;
	  		} else if(key == GLFW_KEY_DOWN) {
	  			/* We can only start moving down, if we're not going up.*/ 
	  			if(MovingDirections.P1_DIRECTION != MovingDirections.UP && MovingDirections.P1_DIRECTION != MovingDirections.DOWN) {
	  				MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.DOWN);
	  			}
	  			return;
	  		}
  		}
		if(key == GLFW_KEY_SPACE) {
  			//System.out.println("PAUSING");
  			GAME_STATUS.pause();
  			return;
  		} else if(key == GLFW_KEY_ENTER) {
  			/* Only allow to start the game if the game is not being played or has ended. */
  			if(!GAME_STATUS.isPlayed() || GAME_STATUS.hasEnded()) {
  				GAME_STATUS.start();
  			}
  			return;
  		} else if(key == GLFW_KEY_ESCAPE) {
  			/* Only allow to exit if */
  			if(GAME_STATUS.isPaused()) {
	  			GAME_STATUS.end();
	  			glfwDestroyWindow(0);
  			}
  			return;
  		}		
	}
}
