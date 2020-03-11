package lv.herbis.cnakes.levels.singleplayer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lv.herbis.cnakes.configuration.Configuration;
import lv.herbis.cnakes.controls.Direction;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import lv.herbis.cnakes.entities.PointCoordinates;
import lv.herbis.cnakes.entities.Timer;
import lv.herbis.cnakes.listeners.SinglePlayerKeyListener;
import lv.herbis.cnakes.save.HighScore;
import lv.herbis.cnakes.save.HighScores;
import lv.herbis.cnakes.staticaccess.GameRules;
import lv.herbis.cnakes.staticaccess.MovingDirections;
import lv.herbis.cnakes.status.SinglePlayerGameStatus;
import lv.herbis.cnakes.tools.SerializationUtil;
import other.fontloader.Color4f;
import other.fontloader.FontTT;

import javax.swing.filechooser.FileSystemView;


public class LevelScreen implements Runnable {
	
	private static final String SAVE_FILE_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\My Games\\cnakes\\";
	private static final String HIGHSCORE_FILE = "classic.hs";
	private static int GAME_LENGTH = 1;
	private static long windowId;

	private final int SCOREBOARD_HEIGHT = 100; // TODO should be scaled

	private HighScores highScores;
	private Configuration configuration;

	private boolean fullScreen;

	private int screenWidth;
    private int screenHeight;
	private int gameBoundX;
	private int gameBoundY;
	private int SCALE = 20; // 10
	private int MOVE_EVERY_MS = 40; //40
    
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

	public LevelScreen(final Configuration configuration)
	{
		this.configuration = configuration;
		initConfiguration();
	}
    
	/**
	 * Cleans up (releases) the resources and destroys the window. 
	 */
	private void cleanUp() {
		glfwFreeCallbacks(windowId);
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

		final long headXScaled = head.X * SCALE;
		final long headXScaledPlus = headXScaled + SCALE;
		final long headYScaled = head.Y * SCALE;
		final long headYScaledPlus = headYScaled + SCALE;
		int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);

		resetToBasicPlayGridLineColor();

		glBegin(GL_LINES);

		/* Vertical lines */
		for (int x = SCALE; x <= gameBoundX; x += SCALE) {
			if (headXScaled == x || headXScaledPlus == x) {
				drawVerticalBrightPlayGridLineBasedOnDirection(x,0, gameBoundY, direction);
				resetToBasicPlayGridLineColor();
			} else {
				drawPlayGridLine(x, x, 0, gameBoundY);
			}
		}

		/* Horizontal lines */
		for (int y = SCALE; y <= gameBoundY; y += SCALE) {
			if (headYScaled == y || headYScaledPlus == y) {
				drawHorizontalBrightPlayGridLineBasedOnDirection(0, gameBoundX, y, direction);
				resetToBasicPlayGridLineColor();
			} else {
				drawPlayGridLine(0, gameBoundX, y, y);
			}
		}		

		glEnd();				 
	}


	/**
	 * Draws a horizontal bright play grid line based on the direction of player.
	 * @param x1 x start coordinates
	 * @param x2 x end coordinates
	 * @param y y coordinates
	 * @param direction player movement direction.
	 */
	private void drawHorizontalBrightPlayGridLineBasedOnDirection(float x1, float x2, float y, final int direction)
	{
		if (direction == Direction.DOWN || direction == Direction.UP) {
			/* draw less brighter */
			glColor3f(0.22f, 0.29f, 0.15f); // TODO configure colors elsewhere (config file?)
			drawPlayGridLine(x1, x2, y, y);
		} else {
			/* draw brighter */
			glColor3f(0.42f, 0.49f, 0.35f); // TODO configure colors elsewhere (config file?)
			drawPlayGridLine(x1, x2, y, y);
		}
	}

	/**
	 * Draws a vertical bright play grid line based on the direction of player.
	 * @param x x coordinates.
	 * @param y1 y start coordinates.
	 * @param y2 y end coordinates.
	 * @param direction player movement direction.
	 */
	private void drawVerticalBrightPlayGridLineBasedOnDirection(float x, float y1, float y2, final int direction)
	{
		if (direction == Direction.DOWN || direction == Direction.UP) {
			/* draw brighter */
			glColor3f(0.42f, 0.49f, 0.35f); // TODO configure colors elsewhere (config file?)
			drawPlayGridLine(x, x, y1, y2);
		} else {
			/* draw less brighter */
			glColor3f(0.22f, 0.29f, 0.15f); // TODO configure colors elsewhere (config file?)
			drawPlayGridLine(x, x, y1, y2);
		}
	}

	/**
	 * Reset color back to basic play grid line color.
	 */
	private void resetToBasicPlayGridLineColor()
	{
		glColor3f(0.22f, 0.29f, 0.15f); // TODO configure colors elsewhere (config file?)
	}

	/**
	 * Draw a play grid line.
	 * @param x1 x start coordinates.
	 * @param x2 x end coordinates.
	 * @param y1 y start coordinates.
	 * @param y2 y end coordinates.
	 */
	private void drawPlayGridLine(float x1, float x2, float y1, float y2)
	{
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
	}
	
	/**
	 * Draws scoreboard on the screen.
	 */
	private void drawScoreboard() {
		
		
		/* Draw score icons */
		glBegin(GL_QUADS);
			/* "Bugs Eaten" square */
			glColor3f(0.25f, 0.73f, 0.31f);
			drawFilledSquare(1, gameBoundY / SCALE + 6);
			
			/* "Snake Length" square */
			glColor3f(0.55f, 0.01f, 0.31f);
			drawFilledSquare(1, gameBoundY / SCALE + 4);
			
			/* "Score" square */
			glColor3f(1.35f, 0.44f, 2.55f);
			drawFilledSquare(1, gameBoundY / SCALE + 2);
			

		glEnd();
		
		/* Draw numbers */
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_ALPHA);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		/* "Bugs Eaten" number */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("" + GAME_STATUS.getBugsCollected(), 20, 25, screenHeight - 26, 0, Color4f.YELLOW, 0, 0, 0, false);
		
		/* "Snake Length" number */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("" + GAME_STATUS.getSnakeLength(), 20, 25, screenHeight - 46, 0, Color4f.YELLOW, 0, 0, 0, false);

		/* "Score" number */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("" + GAME_STATUS.getScore(), 20, 25, screenHeight - 66, 0, Color4f.YELLOW, 0, 0, 0, false);
		
		/* "Player name" text */ //TODO got to make the coordinates and sizes more dynamic
		GAME_FONT.drawText("Player 1", 20, 10, screenHeight - 4, 0, Color4f.YELLOW, 0, 0, 0, false);
		
		if(GAME_STATUS.isPaused()) {
			GAME_FONT.drawText("PAUSED", 40, gameBoundX / 2, screenHeight - 4, 0, Color4f.YELLOW, 0, 0, 0, true);
		}
		glDisable(GL_TEXTURE_2D);
		
		
		drawTime();
	}
	
	
	public void drawSmoothTheSnakeMovement(long pixelAmount) {
		int direction = MovingDirections.getPreviousDirection(MovingDirections.PLAYER_1);

		boolean drawExtraHeadBit = body.size() > 0;
		
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
			GAME_FONT.drawText("Start the Game!", 40, gameBoundX / 2, screenHeight - 44, 0, Color4f.GREEN, 0, 0, 0, true); // TODO reduce calculations
		} else {
			if(GAME_STATUS.hasEnded()) {
				/* If time left is equal to 0, the game has ended. */ 
				GAME_FONT.drawText("Game Over", 40, gameBoundX / 2, screenHeight - 44, 0, Color4f.RED, 0, 0, 0, true); // TODO reduce calculations
				HighScore topScore = highScores.getTopScore();
				GAME_FONT.drawText("Top High Score: " + topScore.getScore() + " (" 
									+ highScores.getTopScore().getUsername() + ") " 
									+ HS_DATE_FORMAT.format(new Date(topScore.getTimestamp())), 
									  20, gameBoundX / 2, screenHeight - 14, 0, Color4f.WHITE, 0, 0, 0, true); // TODO reduce calculations

			} else {
				/* If time left has a value higher than zero, lets use it, to show how much time player has left. */
				DATE_FOR_TIMER.setTime(GAME_STATUS.getTimer().getTimeLeft());
				GAME_FONT.drawText("" + TIME_FORMAT.format(DATE_FOR_TIMER), 40, gameBoundX / 2, screenHeight - 44, 0, Color4f.RED, 0, 0, 0, true); // TODO reduce calculations
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
		while(!glfwWindowShouldClose(windowId)) {
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

	private void initConfiguration() {
		fullScreen = configuration.getVideo().getResolution().isFullScreen();
    	screenWidth = configuration.getVideo().getResolution().getHorizontal();
		screenHeight = configuration.getVideo().getResolution().getVertical();
		gameBoundX = screenWidth;
		gameBoundY = screenHeight - SCOREBOARD_HEIGHT;
	}
	
    
	/**
	 * Initializes the display / window. 
	 */
	private void initDisplay() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		//glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

		windowId = glfwCreateWindow(screenWidth, screenHeight, "Cnakes", fullScreen ? glfwGetPrimaryMonitor() : 0, NULL);
		if ( windowId == NULL ) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
			windowId,
			(vidmode.width() - screenWidth) / 2,
			(vidmode.height() - screenHeight) / 2
		);
		

		
		glfwMakeContextCurrent(windowId);
		glfwSwapInterval(0);
		/*glfwSetWindowSizeCallback(windowId, winSizeCallback);
		glfwSetKeyCallback(windowId, winKeyCallback);
		glfwSetWindowPosCallback(windowId, winPosCallback);
		glfwSetCursorPosCallback(windowId, winCurPosCallback);*/
		GL.createCapabilities();
	}
    
    
	/**
	 * Initializes the game. 
	 */
	private void initGame() {
		loadHighScores();
		startGame();
		//glfwSetInputMode(windowId, GLFW_STICKY_KEYS, 5);
		glfwSetKeyCallback(windowId, new SinglePlayerKeyListener(GAME_STATUS));
	}
	
	
	/**
	 * Initializes Open GL. 
	 */
	private void initGL() {
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, screenWidth, 0, screenHeight, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		
		glClearColor(0, 0, 0, 1);
		
		glDisable(GL_DEPTH_TEST);
		
		
	}
	
	
	/**
	 * Initializes textures that we will use.
	 */
	private void initTextures() {
		try {
			GAME_FONT = new FontTT(Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("fonts/telegrama_raw.otf")), 36, 0);
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
			highScores = (HighScores) SerializationUtil.deserialize(SAVE_FILE_PATH, HIGHSCORE_FILE);
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
			target = new PointCoordinates(random.nextInt(gameBoundX / SCALE), random.nextInt(gameBoundY / SCALE));
		} else {
			/* Create a new target, but make sure it's not in the same spot as the old one. */
			PointCoordinates newTarget = new PointCoordinates(random.nextInt(gameBoundX / SCALE), random.nextInt(gameBoundY / SCALE));
			while(newTarget.equals(target)) {
				newTarget = new PointCoordinates(random.nextInt(gameBoundX / SCALE), random.nextInt(gameBoundY / SCALE));
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
						SerializationUtil.serialize(highScores, SAVE_FILE_PATH, HIGHSCORE_FILE);
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
				if(head.X + 1 < (gameBoundX / SCALE) && hitsTail(head.X + 1, head.Y)) { // maybe eliminate pointless game bound calculations?
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
				if(head.Y + 1 < (gameBoundY / SCALE) && hitsTail(head.X, head.Y + 1)) {
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
