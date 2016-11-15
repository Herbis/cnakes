package com.thehshow.cnakes.staticaccess;

import java.util.LinkedList;
import java.util.Queue;

public class MovingDirections {

	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	public static final int PLAYER_1 = 1, PLAYER_2 = 2, PLAYER_3 = 3, PLAYER_4 = 4, 
							PLAYER_5 = 5, PLAYER_6 = 6, PLAYER_7 = 7, PLAYER_8 = 8;
	public static Queue<Integer> P1_DIRECTION_Q;
	public static int P1_DIRECTION;
	public static int P1_LAST_DIRECTION;
	public static Queue<Integer> P2_DIRECTION_Q;
	public static int P2_DIRECTION;
	public static int P2_LAST_DIRECTION;
	public static Queue<Integer> P3_DIRECTION_Q;
	public static int P3_DIRECTION;
	public static int P3_LAST_DIRECTION;
	public static Queue<Integer> P4_DIRECTION_Q;
	public static int P4_DIRECTION;
	public static int P4_LAST_DIRECTION;
	public static Queue<Integer> P5_DIRECTION_Q;
	public static int P5_DIRECTION;
	public static int P5_LAST_DIRECTION;
	public static Queue<Integer> P6_DIRECTION_Q;
	public static int P6_DIRECTION;
	public static int P6_LAST_DIRECTION;
	public static Queue<Integer> P7_DIRECTION_Q;
	public static int P7_DIRECTION;
	public static int P7_LAST_DIRECTION;
	public static Queue<Integer> P8_DIRECTION_Q;
	public static int P8_DIRECTION;
	public static int P8_LAST_DIRECTION;

	
	/**
	 * Sets Direction the player should go to (adds the direction to queue). 
	 * @param player Which player to set it for.
	 * @param direction Which direction the player should go. 
	 */
	public static void setDirection(int player, int direction) {
		
		switch(player) {
		case PLAYER_1:
			MovingDirections.P1_DIRECTION_Q.add(direction);
			MovingDirections.P1_DIRECTION = direction;
			break;
		case PLAYER_2:
			MovingDirections.P2_DIRECTION_Q.add(direction);
			MovingDirections.P2_DIRECTION = direction;
			break;
		case PLAYER_3:
			MovingDirections.P3_DIRECTION_Q.add(direction);
			MovingDirections.P3_DIRECTION = direction;
			break;
		case PLAYER_4:
			MovingDirections.P4_DIRECTION_Q.add(direction);
			MovingDirections.P4_DIRECTION = direction;
			break;
		case PLAYER_5:
			MovingDirections.P5_DIRECTION_Q.add(direction);
			MovingDirections.P5_DIRECTION = direction;
			break;
		case PLAYER_6:
			MovingDirections.P6_DIRECTION_Q.add(direction);
			MovingDirections.P6_DIRECTION = direction;
			break;
		case PLAYER_7:
			MovingDirections.P7_DIRECTION_Q.add(direction);
			MovingDirections.P7_DIRECTION = direction;
			break;
		case PLAYER_8:
			MovingDirections.P8_DIRECTION_Q.add(direction);
			MovingDirections.P8_DIRECTION = direction;
			break;
		}
	}
	
	public static int getDirection(int player) {
		int direction = -1; 
		
		switch(player) {
		case PLAYER_1:
			if(MovingDirections.P1_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P1_DIRECTION;
			} else {
				direction = MovingDirections.P1_DIRECTION_Q.poll();
			}
			
			MovingDirections.P1_LAST_DIRECTION = direction;
			
			break;
		case PLAYER_2:
			if(MovingDirections.P2_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P2_DIRECTION;
			} else {
				direction = MovingDirections.P2_DIRECTION_Q.poll();
			}
			
			MovingDirections.P2_LAST_DIRECTION = direction;

			break;
		case PLAYER_3:
			if(MovingDirections.P3_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P3_DIRECTION;
			} else {
				direction = MovingDirections.P3_DIRECTION_Q.poll();
			}
			
			MovingDirections.P3_LAST_DIRECTION = direction;

			break;
		case PLAYER_4:
			if(MovingDirections.P4_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P4_DIRECTION;
			} else {
				direction = MovingDirections.P4_DIRECTION_Q.poll();
			}
			
			MovingDirections.P4_LAST_DIRECTION = direction;

			break;
		case PLAYER_5:
			if(MovingDirections.P5_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P5_DIRECTION;
			} else {
				direction = MovingDirections.P5_DIRECTION_Q.poll();
			}
			
			MovingDirections.P5_LAST_DIRECTION = direction;

			break;
		case PLAYER_6:
			if(MovingDirections.P6_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P6_DIRECTION;
			} else {
				direction = MovingDirections.P6_DIRECTION_Q.poll();
			}
			
			MovingDirections.P6_LAST_DIRECTION = direction;

			break;
		case PLAYER_7:
			if(MovingDirections.P7_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P7_DIRECTION;
			} else {
				direction = MovingDirections.P7_DIRECTION_Q.poll();
			}
			
			MovingDirections.P7_LAST_DIRECTION = direction;

			break;
		case PLAYER_8:
			if(MovingDirections.P8_DIRECTION_Q.isEmpty()) {
				direction = MovingDirections.P8_DIRECTION;
			} else {
				direction = MovingDirections.P8_DIRECTION_Q.poll();
			}
			
			MovingDirections.P8_LAST_DIRECTION = direction;
			
			break;
		}
		
		return direction; 
	}
	
	
	public static int getPreviousDirection(int player) {
		
		switch(player) {
		case PLAYER_1:
			return MovingDirections.P1_LAST_DIRECTION;

		case PLAYER_2:
			return MovingDirections.P2_LAST_DIRECTION;

		case PLAYER_3:
			return MovingDirections.P3_LAST_DIRECTION;

		case PLAYER_4:
			return MovingDirections.P4_LAST_DIRECTION;

		case PLAYER_5:
			return MovingDirections.P5_LAST_DIRECTION;

		case PLAYER_6:
			return MovingDirections.P6_LAST_DIRECTION;

		case PLAYER_7:
			return MovingDirections.P7_LAST_DIRECTION;

		case PLAYER_8:
			return MovingDirections.P8_LAST_DIRECTION;
		}
		
		return -1; 
	}
	
	public static void resetDirection(int player) {
		switch(player) {
		case PLAYER_1:
			if(MovingDirections.P1_DIRECTION_Q == null) {
				MovingDirections.P1_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P1_DIRECTION_Q.clear();
			}
			MovingDirections.P1_DIRECTION = MovingDirections.UP;
			MovingDirections.P1_LAST_DIRECTION = MovingDirections.UP;
			
			break;
		case PLAYER_2:
			if(MovingDirections.P2_DIRECTION_Q == null) {
				MovingDirections.P2_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P2_DIRECTION_Q.clear();
			}
			MovingDirections.P2_DIRECTION = MovingDirections.UP;
			MovingDirections.P2_LAST_DIRECTION = MovingDirections.UP;

			break;
		case PLAYER_3:
			if(MovingDirections.P3_DIRECTION_Q == null) {
				MovingDirections.P3_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P3_DIRECTION_Q.clear();
			}
			MovingDirections.P3_DIRECTION = MovingDirections.UP;
			MovingDirections.P3_LAST_DIRECTION = MovingDirections.UP;
			
			break;
		case PLAYER_4:
			if(MovingDirections.P4_DIRECTION_Q == null) {
				MovingDirections.P4_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P4_DIRECTION_Q.clear();
			}
			MovingDirections.P4_DIRECTION = MovingDirections.UP;
			MovingDirections.P4_LAST_DIRECTION = MovingDirections.UP;
			
			break;
		case PLAYER_5:
			if(MovingDirections.P5_DIRECTION_Q == null) {
				MovingDirections.P5_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P5_DIRECTION_Q.clear();
			}
			MovingDirections.P5_DIRECTION = MovingDirections.UP;
			MovingDirections.P5_LAST_DIRECTION = MovingDirections.UP;
			
			break;
		case PLAYER_6:
			if(MovingDirections.P6_DIRECTION_Q == null) {
				MovingDirections.P6_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P6_DIRECTION_Q.clear();
			}
			MovingDirections.P6_DIRECTION = MovingDirections.UP;
			MovingDirections.P6_LAST_DIRECTION = MovingDirections.UP;
			
			break;
		case PLAYER_7:
			if(MovingDirections.P7_DIRECTION_Q == null) {
				MovingDirections.P7_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P7_DIRECTION_Q.clear();
			}
			MovingDirections.P7_DIRECTION = MovingDirections.UP;
			MovingDirections.P7_LAST_DIRECTION = MovingDirections.UP;
			

			break;
		case PLAYER_8:
			if(MovingDirections.P8_DIRECTION_Q == null) {
				MovingDirections.P8_DIRECTION_Q = new LinkedList<Integer>();
			} else {
				MovingDirections.P8_DIRECTION_Q.clear();
			}
			MovingDirections.P8_DIRECTION = MovingDirections.UP;
			MovingDirections.P8_LAST_DIRECTION = MovingDirections.UP;
			
			break;
		}
	}
}
