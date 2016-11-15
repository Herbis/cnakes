package com.thehshow.cnakes.levels.singleplayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GLContext;

import other.fontloader.Color4f;
import other.fontloader.FontTT;

import com.thehshow.cnakes.entities.PointCoordinates;
import com.thehshow.cnakes.entities.Timer;
import com.thehshow.cnakes.listeners.SinglePlayerKeyListener;
import com.thehshow.cnakes.save.HighScore;
import com.thehshow.cnakes.save.HighScores;
import com.thehshow.cnakes.staticaccess.GameRules;
import com.thehshow.cnakes.staticaccess.MovingDirections;
import com.thehshow.cnakes.status.SinglePlayerGameStatus;
import com.thehshow.cnakes.tools.SerializationUtil;


public class LevelScreen implements Runnable {
	
	/* Callbacks, needed to avoid them being garbage collected. Have to be in the main class. */
	private static GLFWErrorCallback errorCallback;
	private static GLFWWindowSizeCallback winSizeCallback;
	private static GLFWKeyCallback winKeyCallback;
	private static GLFWWindowPosCallback winPosCallback;
	private static GLFWCursorPosCallback winCurPosCallback;
	
	private static final String SAVE_FILE_PATH = "C:/Users/Herbis/Documents/cnakes/";
	private static final String HIGHSCORE_FILE = "classic.hs";
	private HighScores highScores;
	
	private static int GAME_LENGTH = 1;
	
	private static long windowId;
	int WIDTH = 800;
    int HEIGHT = 600;
    int GAME_BOUND_X = WIDTH;
    int GAME_BOUND_Y = HEIGHT - 100;
    int SCALE = 10; // 10
    int MOVE_EVERY_MS = 40; //40
    
    boolean halfCellReached = true;
    
    SinglePlayerGameStatus GAME_STATUS;
    PointCoordinates head;
    PointCoordinates target;
    List<PointCoordinates> body; 
    Random random;
    Date DATE_FOR_TIMER = new Date();
    
    SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss.SSS");
    SimpleDateFormat HS_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    
    /** time at last frame */
    long lastFrame;
    long lastDelta = 0;

     
    /** frames per second */
    int fps;
    /** last fps time */
    long lastFPS;
    
	/** The fonts to draw to the screen */
	private FontTT GAME_FONT;

    
	/**
	 * Cleans up (releases) the resources and destroys the window. 
	 */
	private void cleanUp() {
		errorCallback.release();
		winSizeCallback.release();
		winKeyCallback.release();
		winPosCallback.release();
		winCurPosCallback.release();
		glfwDestroyWindow(windowId);

	}
	
	
	/**
	 * Draws a filled square based on given coordinates.
	 * Set colour and call glBegin(GL_QUADS) before calling this, and call glEnd afterwards.
	 */
	private void drawFilledSquare(long x, long y) {
		glVertex2f(x * SCALE, y * SCALE + SCALE); // top left
		glVertex2f(x * SCALE, y * SCALE); // bottom left 
		glVertex2f((x * SCALE + SCALE), y * SCALE); // bottom right
		glVertex2f((x * SCALE + SCALE), y * SCALE + SCALE); // top right
	}
    
	
	/**
	 * Draws a filled square based on given coordinates.
	 * Set colour and call glBegin(GL_QUADS) before calling this, and call glEnd afterwards.
	 */
	private void drawFilledUnscaledSquare(long xLeft, long xRight, long yBottom, long longTop) {
		glVertex2f(xLeft, longTop); // top left
		glVertex2f(xLeft, yBottom); // bottom left 
		glVertex2f(xRight, yBottom); // bottom right
		glVertex2f(xRight , longTop); // top right
	}
    
    /**
     * Draws the grid the snake is moving on (or under to be more precise) on the screen.  
     */
	private void drawPlayGrid() {

		glColor3f(0.22f, 0.29f, 0.15f);

		glBegin(GL_LINES);
		for(int i = SCALE; i <= WIDTH; i += SCALE){
			glVertex2f(i, 0);
			glVertex2f(i, GAME_BOUND_Y);
		}		
		
		for(int i = SCALE; i <= GAME_BOUND_Y; i += SCALE){
			glVertex2f(0, i);
			glVertex2f(WIDTH, i);
		}		

		glEnd();				 
	}
	
	/**
	 * Draws scoreboard on the screen.
	 */
	private void drawScoreboard() {
		
		
		/* Draw score icons */
		glBegin(GL_QUADS);
			/* "Bugs Eaten" square */
			glColor3f(0.25f, 0.73f, 0.31f);
			drawFilledSquare(1, GAME_BOUND_Y / SCALE + 6);
			
			/* "Snake Length" square */
			glColor3f(0.55f, 0.01f, 0.31f);
			drawFilledSquare(1, GAME_BOUND_Y / SCALE + 4);
			
			/* "Score" square */
			glColor3f(1.35f, 0.44f, 2.55f);
			drawFilledSquare(1, GAME_BOUND_Y / SCALE + 2);
			

		glEnd();
		
		/* Draw numbers */
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_ALPHA);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		/* "Bugs Eaten" number */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("" + GAME_STATUS.getBugsCollected(), 20, 25, 574, 0, Color4f.YELLOW, 0, 0, 0, false);
		
		/* "Snake Length" number */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("" + GAME_STATUS.getSnakeLength(), 20, 25, 554, 0, Color4f.YELLOW, 0, 0, 0, false);

		/* "Score" number */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("" + GAME_STATUS.getScore(), 20, 25, 534, 0, Color4f.YELLOW, 0, 0, 0, false);
		
		/* "Player name" text */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("Player 1", 20, 10, 596, 0, Color4f.YELLOW, 0, 0, 0, false);
		
		if(GAME_STATUS.isPaused()) {
			GAME_FONT.drawText("PAUSED", 40, GAME_BOUND_X / 2, 596, 0, Color4f.YELLOW, 0, 0, 0, true);
		}
		glDisable(GL_TEXTURE_2D);
		
		
		drawTime();
	}
	
	
	public void drawSmoothTheSnakeMovement(long pixelAmount) {
		int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);

		boolean drawExtraHeadBit = body.size() > 0 ? true : false; 
		
		glColor3f(0.55f, 0.01f, 0.31f); //TODO probably make configurable / global
		if(direction == MovingDirections.DOWN) {	
			if(halfCellReached) {
				/* Draw the head. */
				drawUnfilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
						   (head.Y * SCALE) - pixelAmount + SCALE, (head.Y * SCALE) + SCALE - pixelAmount + SCALE, 3);
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 

					drawFilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
							   (head.Y * SCALE) + SCALE - 1 - pixelAmount  + SCALE, (head.Y * SCALE) + (SCALE * 2));
					glEnd();
				}
			} else {
				drawUnfilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
						   (head.Y * SCALE) - pixelAmount, (head.Y * SCALE) + SCALE - pixelAmount, 3);
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
							   (head.Y * SCALE) + SCALE - 1 - pixelAmount, (head.Y * SCALE) + SCALE);
					glEnd();
				}
			}
			

		} else if (direction == MovingDirections.LEFT) {
			/* Draw the head. */
			if(halfCellReached) {
				drawUnfilledUnscaledSquare((head.X * SCALE) - pixelAmount + SCALE, (head.X * SCALE) + SCALE - pixelAmount + SCALE, 
										   (head.Y * SCALE), (head.Y * SCALE) + SCALE, 3);
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE) + SCALE - pixelAmount - 1 + SCALE, (head.X * SCALE) + SCALE + SCALE, 
							   (head.Y * SCALE), (head.Y * SCALE) + SCALE);
					glEnd();
				}
			} else {
				drawUnfilledUnscaledSquare((head.X * SCALE) - pixelAmount, (head.X * SCALE) + SCALE - pixelAmount, 
						   (head.Y * SCALE), (head.Y * SCALE) + SCALE, 3);
				
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE) + SCALE - pixelAmount - 1, (head.X * SCALE) + SCALE , 
							   (head.Y * SCALE), (head.Y * SCALE) + SCALE);
					glEnd();
				}
			}

		} else if (direction == MovingDirections.RIGHT) {
			if(halfCellReached) {
				/* Draw the head. */
				drawUnfilledUnscaledSquare((head.X * SCALE) + pixelAmount - SCALE, (head.X * SCALE) + SCALE + pixelAmount - SCALE, 
										   (head.Y * SCALE), (head.Y * SCALE) + SCALE, 3);
				
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE) - SCALE, (head.X * SCALE) + 1 + pixelAmount - SCALE, 
							   (head.Y * SCALE), (head.Y * SCALE) + SCALE);
					glEnd();
				}
			} else {
				/* Draw the head. */
				drawUnfilledUnscaledSquare((head.X * SCALE) + pixelAmount, (head.X * SCALE) + SCALE + pixelAmount, 
										   (head.Y * SCALE), (head.Y * SCALE) + SCALE, 3);
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + 1 + pixelAmount, 
							   (head.Y * SCALE), (head.Y * SCALE) + SCALE);
					glEnd();
				}
			}

		} else if (direction == MovingDirections.UP) {
			if(halfCellReached) {
				/* Draw the head. */
				drawUnfilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
										   (head.Y * SCALE) + pixelAmount - SCALE, (head.Y * SCALE) + SCALE + pixelAmount - SCALE, 3);
				
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
							(head.Y * SCALE) - SCALE, (head.Y * SCALE) + 1 + pixelAmount - SCALE);
					glEnd();
				}
			} else {
				/* Draw the head. */
				drawUnfilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
										   (head.Y * SCALE) + pixelAmount, (head.Y * SCALE) + SCALE + pixelAmount, 3);
				
				if(drawExtraHeadBit) {
					glBegin(GL_QUADS); // Must 
					drawFilledUnscaledSquare((head.X * SCALE), (head.X * SCALE) + SCALE, 
							(head.Y * SCALE), (head.Y * SCALE) + 1 + pixelAmount);
					glEnd();
				}
			}
		}
		
		/* Draw the body. */
		glBegin(GL_QUADS); // Must 

		if(body.size() > 0) {
			/* We need to determine which snake part we need to smooth to. */ 	
			PointCoordinates smoothTo;
			if(body.size() > 1) {
				smoothTo = body.get(1); // Smooth to body part before the last one.
				int bodySize = body.size();
				if(halfCellReached) {
					bodySize = bodySize - 1;
				}
				for(int i = 1; i < bodySize; i++) {
					PointCoordinates bodyPart = body.get(i);
					drawFilledSquare(bodyPart.X, bodyPart.Y);
				}
				
			} else { 
				smoothTo = head; // Smooth to head.
			}
			PointCoordinates lastBodyPart = body.get(0);
			
			
			glBegin(GL_QUADS); // Must 
			if(smoothTo.X - lastBodyPart.X != 0) { // Smooth on the X axis
				if(smoothTo.X - lastBodyPart.X == 1) { // Smooth to left
					if(halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE) + pixelAmount - SCALE, (lastBodyPart.X * SCALE) + SCALE,
						  	(lastBodyPart.Y * SCALE), (lastBodyPart.Y * SCALE) + SCALE);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE) + pixelAmount, (lastBodyPart.X * SCALE) + SCALE,
							  	(lastBodyPart.Y * SCALE), (lastBodyPart.Y * SCALE) + SCALE);
					}
				} else { // Smooth to right
					if(halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE), (lastBodyPart.X * SCALE) + SCALE - pixelAmount + SCALE,
					  		 (lastBodyPart.Y * SCALE), (lastBodyPart.Y * SCALE) + SCALE);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE) - SCALE, (lastBodyPart.X * SCALE) + SCALE - pixelAmount,
						  		 (lastBodyPart.Y * SCALE), (lastBodyPart.Y * SCALE) + SCALE);
					}
				}
			} else if (smoothTo.Y - lastBodyPart.Y != 0) { // Smooth on the Y axis 
				if(smoothTo.Y - lastBodyPart.Y == 1) { // Smooth up
					if(halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE), (lastBodyPart.X * SCALE) + SCALE,
					  		 (lastBodyPart.Y * SCALE) + pixelAmount - SCALE, (lastBodyPart.Y * SCALE) + SCALE );
					} else {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE), (lastBodyPart.X * SCALE) + SCALE,
						  		 (lastBodyPart.Y * SCALE) + pixelAmount, (lastBodyPart.Y * SCALE) + SCALE);
					}
				} else { // Smooth down
					if(halfCellReached) {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE), (lastBodyPart.X * SCALE) + SCALE,
					  		 (lastBodyPart.Y * SCALE) - SCALE, (lastBodyPart.Y * SCALE) + SCALE - pixelAmount + SCALE);
					} else {
						drawFilledUnscaledSquare((lastBodyPart.X * SCALE), (lastBodyPart.X * SCALE) + SCALE,
						  		 (lastBodyPart.Y * SCALE) , (lastBodyPart.Y * SCALE) + SCALE - pixelAmount);
					}
				}
			}
			glEnd();
		}
	}
	
	
	/**
	 * Draws the Snake on the screen. 
	 */
	private void drawSnake() {
		glColor3f(0.55f, 0.01f, 0.31f); //TODO probably make configurable / global
		
		/* Draw the head. */
		drawUnfilledSquare(head.X, head.Y, 3);

		/* Draw the body. */
		glBegin(GL_QUADS); // Must 
		for(PointCoordinates bodyPart : body) {
				drawFilledSquare(bodyPart.X, bodyPart.Y);
		}
		glEnd();
	}
	
	
	/**
	 * Draws whatever is supposted to be shown on the game start screen. 
	 */
	private void drawStartScreen() {
		
		glColor3f(0.55f, 0.11f, 0.15f);
		
		glBegin(GL_QUADS);
			glVertex2f(5 * SCALE, 40 * SCALE); // top left
			glVertex2f(5* SCALE, 39 * SCALE); // bottom left 
			glVertex2f(6 * SCALE, 39 * SCALE); // bottom right
			glVertex2f(6 * SCALE, 40 * SCALE); // top right
		glEnd();
	}
	
	
	/**
	 * Draws the target (bug) on the screen).
	 */
	private void drawTarget() {
		glColor3f(0.25f, 0.73f, 0.31f); //TODO probably make configurable / global
		glBegin(GL_QUADS);
			drawFilledSquare(target.X, target.Y);
		glEnd();
	}
	
	
	/**
	 * Draws Game Time left on the screen. 
	 */
	public void drawTime() {
		/* Draw numbers */
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_ALPHA);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Timer currentTimer = GAME_STATUS.getTimer();
		if(currentTimer == null) {
			/* If the current Timer is null, the game was never started. */
			GAME_FONT.drawText("Start the Game!", 40, GAME_BOUND_X / 2, 556, 0, Color4f.GREEN, 0, 0, 0, true);
		} else {
			if(GAME_STATUS.hasEnded()) {
				/* If time left is equal to 0, the game has ended. */ 
				GAME_FONT.drawText("Game Over", 40, GAME_BOUND_X / 2, 556, 0, Color4f.RED, 0, 0, 0, true);
				HighScore topScore = highScores.getTopScore();
				GAME_FONT.drawText("Top High Score: " + topScore.getScore() + " (" 
									+ highScores.getTopScore().getUsername() + ") " 
									+ HS_DATE_FORMAT.format(new Date(topScore.getTimestamp())), 
									  20, GAME_BOUND_X / 2, 586, 0, Color4f.WHITE, 0, 0, 0, true);

			} else {
				/* If time left has a value higher than zero, lets use it, to show how much time player has left. */
				DATE_FOR_TIMER.setTime(GAME_STATUS.getTimer().getTimeLeft());
				GAME_FONT.drawText("" + TIME_FORMAT.format(DATE_FOR_TIMER), 40, GAME_BOUND_X / 2, 556, 0, Color4f.RED, 0, 0, 0, true);
			}
		}
		
		glDisable(GL_TEXTURE_2D);
	}
	
	
	/**
	 * Draws an unfilled square based on given coordinates.
	 * Does the glBegin(GL_LINE_STRIP) here too.
	 */
	private void drawUnfilledSquare(long x, long y, int thickness) {
		glBegin(GL_LINE_STRIP);
		for(int lap = 1; lap <= thickness; lap++) {
			glVertex2f((x * SCALE) + lap , (y * SCALE) + SCALE - lap); // top left
			glVertex2f((x * SCALE) + lap, (y * SCALE) + (lap - 1) ); // bottom left 
			glVertex2f((x * SCALE) + SCALE - lap, y * SCALE + lap); // bottom right
			glVertex2f((x * SCALE) + SCALE - lap, (y * SCALE) + SCALE - lap); // top right
			glVertex2f((x * SCALE) + lap , (y * SCALE) + SCALE - lap); // top left
		}
		glEnd();
	}
	
	/**
	 * Draws an unfilled square based on given coordinates.
	 * Does the glBegin(GL_LINE_STRIP) here too.
	 */
	private void drawUnfilledUnscaledSquare(long xLeft, long xRight, long yBottom, long longTop, int thickness) {
		glBegin(GL_LINE_STRIP);
		for(int lap = 1; lap <= thickness; lap++) {
			glVertex2f(xLeft + lap , longTop - lap); // top left
			glVertex2f(xLeft   + lap, yBottom + (lap - 1) ); // bottom left 
			glVertex2f(xRight - lap, yBottom + lap); // bottom right
			glVertex2f(xRight - lap, longTop - lap); // top right
			glVertex2f(xLeft + lap , longTop - lap); // top left
		}
		glEnd();
	}
	
	
	/**
	 * Starts the game loop, that keeps the game running. 
	 */
	private void gameLoop() {
		while(glfwWindowShouldClose(windowId) == 0) {
			glClear(GL_COLOR_BUFFER_BIT);
			update();
			
			glfwSwapBuffers(windowId);
			try {
				glfwPollEvents(); 
			} catch(NullPointerException e) {
				System.exit(0);
			}
		}
		
	}
	
	
	/** 
     * Calculate how many milliseconds have passed 
     * since last frame.
     * 
     * @return milliseconds passed since last frame 
     */
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;
      
        return delta;
    }
    
    
    /**
     * Get the accurate system time
     * 
     * @return The system time in milliseconds
     */
    public long getTime() {
        return System.nanoTime() / 1000000;
    }
	
    
	/**
	 * Method that checks if the x,y matches any part of the tail.
	 * @param x
	 * @param y
	 * @return
	 */
    public boolean hitsTail(long x, long y) {
    	
		for (int index = 0; index < body.size(); index++){
			PointCoordinates point = body.get(index);
			if (point.equals(new PointCoordinates(x, y))){
				int bonus = 1;
				if(GAME_STATUS.inBonus()) {
					bonus = GameRules.POINTS_PER_POINT;
				}
				
				for(int till = index; till >= 0; till--) {
					body.remove(till);
					GAME_STATUS.addScore(GameRules.POINTS_PER_POINT + (bonus * till));
				}
				
				GAME_STATUS.setSnakeLength(body.size());

				return true;
			}
		}
		return true;
	}
	
    
	/**
	 * Initializes the display / window. 
	 */
	private void initDisplay() {
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // the window will NOT be resizable
		windowId = glfwCreateWindow(WIDTH, HEIGHT, "Cnakes", 0, NULL);
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
	            windowId,
	            (GLFWvidmode.width(vidmode) - WIDTH) / 2,
	            (GLFWvidmode.height(vidmode) - HEIGHT) / 2
	        );
		

		
		
		glfwMakeContextCurrent(windowId);
		glfwSwapInterval(0);
		/*glfwSetWindowSizeCallback(windowId, winSizeCallback);
		glfwSetKeyCallback(windowId, winKeyCallback);
		glfwSetWindowPosCallback(windowId, winPosCallback);
		glfwSetCursorPosCallback(windowId, winCurPosCallback);*/
		GLContext.createFromCurrent();
	}
    
    
	/**
	 * Initializes the game. 
	 */
	private void initGame() {
		loadHighScores();
		startGame();
		//glfwSetInputMode(windowId, GLFW_STICKY_KEYS, 5);
		glfwSetKeyCallback(windowId, winKeyCallback =  new SinglePlayerKeyListener(GAME_STATUS));
	}
	
	
	/**
	 * Initializes Open GL. 
	 */
	private void initGL() {
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 0, 600, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		
		glClearColor(0, 0, 0, 1);
		
		glDisable(GL_DEPTH_TEST);
		
		
	}
	
	
	/**
	 * Initializes textures that we will use.
	 */
	private void initTextures() {
		try {
			GAME_FONT = new FontTT(Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/fonts/telegrama_raw.otf")), 36, 0);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		//TextureLoader loader = new TextureLoader();
		//myTexture = loader.getTexture("",false); //load texture
	}
	
	
	/**
	 * Loads High Scores from a file to the local class.
	 */
	public void loadHighScores() {
		try {
			highScores = (HighScores) SerializationUtil.deserialize(SAVE_FILE_PATH + HIGHSCORE_FILE);
		} catch (Exception e) {
			highScores = new HighScores(10);
		}
	}
	
	
	/**
	 * Creates a new Snake;
	 */
	public void newSnake() {
		head = new PointCoordinates(0, 0);
		body = new ArrayList<PointCoordinates>();
	}
	
	
	/**
	 * Creates a new target (bug). 
	 */
	public void newTarget() {
		if(target == null) {
			/* If we didn't have a target object already, create one. */
			target = new PointCoordinates(random.nextInt(GAME_BOUND_X / SCALE), random.nextInt(GAME_BOUND_Y / SCALE));
		} else {
			/* Create a new target, but make sure it's not in the same spot as the old one. */
			PointCoordinates newTarget = new PointCoordinates(random.nextInt(GAME_BOUND_X / SCALE), random.nextInt(GAME_BOUND_Y / SCALE));
			while(newTarget.equals(target)) {
				newTarget = new PointCoordinates(random.nextInt(GAME_BOUND_X / SCALE), random.nextInt(GAME_BOUND_Y / SCALE));
			}
			
			target.setLocation(newTarget.X, newTarget.Y);
		}
	}
	
	
	/**
	 * Renders all that needs to be rendered for this game.
	 */
	private void render() {
		
		//drawSnake();
		drawTarget();

		drawPlayGrid();

		drawScoreboard();
	}
	
	
    /**
     * Gets called when the thread starts (keeps the game running).
     */
	public void run() {
		System.setProperty("org.lwjgl.librarypath", new File("native").getAbsolutePath());
		
		initDisplay();
		initGL();
		initGame();
		initTextures();

		gameLoop();
		cleanUp();
	}
	
	

	
	
	/**
	 * Starts the playable game. 
	 */
	public void startGame(){
		MovingDirections.resetDirection(MovingDirections.PLAYER_1);
		
		GAME_STATUS = new SinglePlayerGameStatus(Timer.minutesToMiliseconds(GAME_LENGTH)){
			@Override
			public void afterEnd() {
				System.out.println("End of the game.");
				HighScore highScore = new HighScore("Player 1", GAME_STATUS.getScore());
				if(highScores.addHighScore(highScore)) {
					System.out.println("Highscore added.");
					try {
						SerializationUtil.serialize(highScores, SAVE_FILE_PATH + HIGHSCORE_FILE);
					} catch (Exception e) {
						System.out.println("Could not save high score file.");
					}
				} else {
					System.out.println("Highscore was not added.");
				}
			}
		};
		//GAME_STATUS.start();
		
		head = new PointCoordinates(0, 0);
		random = new Random();
		body = new ArrayList<PointCoordinates>();
		newTarget();
		
	}
	
	
	/**
	 * Updates the game.
	 */
	private void update() {
		
		/* Reset Target (bug) and Snake if the game has just been started. */
		if(GAME_STATUS.hasJustStarted()) {
			MovingDirections.resetDirection(MovingDirections.PLAYER_1);
			newSnake();
			newTarget();
		}
		
		/* Update only every few miliseconds. TODO should be configurable, as in game speed. */
		if(!GAME_STATUS.isPaused() && GAME_STATUS.isPlayed() && !GAME_STATUS.hasEnded()) {
			
			
/*			if(halfCellReached) {
				if(lastDelta > MOVE_EVERY_MS) {
					halfCellReached = false;
					lastDelta = getDelta();
				}
			} else {
				if(lastDelta > MOVE_EVERY_MS / 2) {
					halfCellReached = true;
					updateSnakePosition();

			}*/
			

			if(lastDelta > MOVE_EVERY_MS) {
				lastDelta = getDelta();
				halfCellReached = false; 
			
			} else if(lastDelta > MOVE_EVERY_MS / 2 && !halfCellReached) {
				updateSnakePosition();

				halfCellReached = true;
			}
			/* Calculate how much in the cell we should move. */ // 10(lastDelta) * 10(scale) / 40 (move_every_ms) = 2.5
			drawSmoothTheSnakeMovement(lastDelta * SCALE / MOVE_EVERY_MS);
		} else {
			drawSnake();

		}

		render();
		lastDelta += getDelta();
	}

	
	/**
     * Calculate the FPS and set it in the title bar
     */
    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }
	
	
	/**
	 * Updates the snake position.
	 */
	private void updateSnakePosition() {
		int direction = -1;
		if(head != null) {
			
			/* Disallow movement on non whole number coordinates. */
			/*if(head.X % 1 > 1 && head.Y % 1 > 1) {
				System.out.println("Allowed");
				direction = MovingDirections.getDirection(MovingDirections.PLAYER_1);
			} else {
				//System.out.println("Disallowed");
				System.out.println("X" + head.X % 1);
				System.out.println("Y" + head.Y % 1);

				direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);
			}*/
			
			direction = MovingDirections.getDirection(MovingDirections.PLAYER_1);

			
			body.add(new PointCoordinates (head.X, head.Y));

			if(body.size() > GAME_STATUS.getSnakeLength()) {
				body.remove(0);
			}

			
			if(direction == MovingDirections.RIGHT) { 
				if(head.X + 1 < (GAME_BOUND_X / SCALE) && hitsTail(head.X + 1, head.Y)) { // maybe eliminate pointless game bound calculations?
					head = new PointCoordinates(head.X + 1, head.Y);
					
				} else {
					GAME_STATUS.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.LEFT);
				}
			} else if(direction == MovingDirections.LEFT) { 
				if(head.X - 1 >= 0 && hitsTail(head.X - 1, head.Y)) {
					head = new PointCoordinates(head.X - 1, head.Y);
				} else {
					GAME_STATUS.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.RIGHT);
				}
			} else if(direction == MovingDirections.DOWN) {
				//if(head.y + 1 < 43 && tail(head.x, head.y + 1)) {
				if(head.Y - 1 >= 0 && hitsTail(head.X, head.Y - 1)) {

					head = new PointCoordinates(head.X, head.Y - 1);
				} else {
					GAME_STATUS.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.UP);
				}			
			} else if(direction == MovingDirections.UP) { 
				if(head.Y + 1 < (GAME_BOUND_Y / SCALE) && hitsTail(head.X, head.Y + 1)) {
					head = new PointCoordinates(head.X, head.Y + 1);
				} else {
					GAME_STATUS.setInBonus(false);

					/* Set opposite direction. */
					MovingDirections.setDirection(MovingDirections.PLAYER_1, MovingDirections.DOWN);
				}
			}

			if (target != null) {
				if (head.equals(target)) {
					GAME_STATUS.collectBug();
					GAME_STATUS.setInBonus(true);
					newTarget();
				}
			}
		}
			
	}
			
}
