package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.draw.Drawing;
import lv.herbis.cnakes.screens.CnakesScreen;
import lv.herbis.cnakes.listeners.MenuKeyListener;
import lv.herbis.cnakes.movement.MenuNavigation;
import lv.herbis.cnakes.sound.SoundListener;
import lv.herbis.cnakes.sound.SoundManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.awt.*;

import static lv.herbis.cnakes.constants.CnakesConstants.LOG_STACKTRACE;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MainMenu implements Runnable {

	private static final Logger LOG = LogManager.getLogger(MainMenu.class);
	private final Drawing drawing;
	private final CnakesConfiguration configuration;
	private MenuNavigation navigation;

	private boolean resolutionAutoConfig;
	private Integer monitor;

	private long windowId;
	private int screenWidth;
	private int screenHeight;
	private int gameScale;

	private final SoundManager soundManager;

	private CnakesScreen cnakesScreen;

	public MainMenu(final CnakesConfiguration configuration) {
		this.configuration = configuration;
		this.drawing = new Drawing(configuration);
		this.soundManager = new SoundManager();
		initConfiguration();
	}

	private void autoConfigureResolution() {
		final GLFWVidMode vidModes = glfwGetVideoMode(glfwGetPrimaryMonitor());
		this.configuration.getVideo().getResolution().setHorizontal(vidModes.width());
		this.configuration.getVideo().getResolution().setVertical(vidModes.height());
		this.configuration.getVideo().getResolution().setFullScreen(true);
	}

	/**
	 * Cleans up (releases) the resources and destroys the window.
	 */
	private void cleanUp() {
		this.soundManager.cleanup();
		// set cursor back to normal
		glfwSetInputMode(this.windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

		glfwFreeCallbacks(this.windowId);
		glfwDestroyWindow(this.windowId);
	}

	private void initConfiguration() {
		this.resolutionAutoConfig = this.configuration.getVideo().getResolution().isAutoConfig();
		this.monitor = this.configuration.getVideo().getMonitor();
		this.gameScale = this.configuration.getVideo().getScale();
	}

	/**
	 * Initializes the display / window.
	 */
	private void initDisplay() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set(); // NOSONAR

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

		if (this.resolutionAutoConfig) {
			autoConfigureResolution();
		}

		final boolean fullScreen = this.configuration.getVideo().getResolution().isFullScreen();
		this.screenWidth = this.configuration.getVideo().getResolution().getHorizontal();
		this.screenHeight = this.configuration.getVideo().getResolution().getVertical();

		final long fullScreenMonitor;
		if (fullScreen) {
			fullScreenMonitor = this.monitor == null ? glfwGetPrimaryMonitor() : glfwGetMonitors().get(this.monitor);
		} else {
			fullScreenMonitor = NULL;
		}

		this.windowId = glfwCreateWindow(this.screenWidth, this.screenHeight, "Cnakes", fullScreenMonitor, NULL);
		if (this.windowId == NULL) {
			throw new IllegalStateException("Failed to create the GLFW window");
		}

		// Hide cursor
		glfwSetInputMode(this.windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

		// Get the resolution of the primary monitor
		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(this.windowId, (vidmode.width() - this.screenWidth) / 2,
						 (vidmode.height() - this.screenHeight) / 2);

		glfwMakeContextCurrent(this.windowId);
		glfwSwapInterval(0);
		GL.createCapabilities();
	}

	/**
	 * Initializes Open GL.
	 */
	private void initGL() {

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, this.screenWidth, 0, this.screenHeight, -1, 1);
		glMatrixMode(GL_MODELVIEW);

		glClearColor(0, 0, 0, 1);

		glDisable(GL_DEPTH_TEST);
	}

	@Override
	public void run() {
		initDisplay();
		initGL();
		initSound();
		initMenu();
		this.drawing.initFont("fonts/trs-million_rg.ttf");
		gameLoop();
		cleanUp();
	}

	/**
	 * Initializes the game.
	 */
	private void initMenu() {
		this.navigation = new MenuNavigation(this.configuration, this.windowId, this.soundManager);
		glfwSetKeyCallback(this.windowId, new MenuKeyListener(this.navigation, this.windowId));
	}

	private void initSound() {
		this.soundManager.init();

		final SoundListener soundListener = new SoundListener();
		this.soundManager.setListener(soundListener);
	}

	/**
	 * Starts the game loop, that keeps the game running.
	 */
	private void gameLoop() {
		while (!glfwWindowShouldClose(this.windowId)) {
			final Object pendingItem = this.navigation.usePendingItem();
			if (pendingItem instanceof CnakesScreen) {
				this.cnakesScreen = (CnakesScreen) pendingItem;
				this.cnakesScreen.initScreen();
			}

			glClear(GL_COLOR_BUFFER_BIT);

			if (this.cnakesScreen != null) {
				// maybe need to use a separate SCREEN interface.
				this.cnakesScreen.update();
			} else {
				update();
			}

			glfwSwapBuffers(this.windowId);
			try {
				glfwPollEvents();
			} catch (final NullPointerException e) {
				LOG.warn("NullPointerException in Poll Events.");
				LOG.debug(LOG_STACKTRACE, e);
			}
		}
	}

	private void update() {
		final MenuItem[] menuItems = this.navigation.getMenuItems();
		final MenuItem activeItem = this.navigation.getActiveItem();
		final int itemCount = menuItems.length;

		final float scaledHeight = this.screenHeight / (float) this.gameScale;
		final int slots = (int) scaledHeight / 3;
		final int centerSlot = slots / 2;
		final int centerItemIndex = (itemCount / 2);
		final float xLoc = this.screenWidth / ((float) this.gameScale * 2f);


		for (int i = 0; i < itemCount; i++) {
			final MenuItem item = menuItems[i];
			final float itemSize;
			final Color color;
			float adjustment = 0;
			if (activeItem == item) {
				itemSize = 3f;
				color = activeItem.getColor();
				adjustment = 0.5f;
			} else {
				itemSize = 2f;
				color = Color.GRAY;
			}

			final int difference = i - centerItemIndex;
			final int slot = (centerSlot + difference);
			this.drawing.drawText(item.getName(), itemSize, xLoc, scaledHeight - (slot * 3) + adjustment, color, true);
		}
	}
}
