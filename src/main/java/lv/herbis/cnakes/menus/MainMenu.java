package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.draw.Drawing;
import lv.herbis.cnakes.entities.PointCoordinates;
import lv.herbis.cnakes.listeners.MenuKeyListener;
import lv.herbis.cnakes.listeners.SinglePlayerKeyListener;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import other.fontloader.Color4f;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MainMenu implements Runnable {

	private static final Logger LOG = LogManager.getLogger(MainMenu.class);
	private final Drawing drawing;
	private final CnakesConfiguration configuration;
	private MenuNavigation navigation;

	private boolean fullScreen;
	private Integer monitor;

	private long windowId;
	private int screenWidth;
	private int screenHeight;
	private int gameScale;

	public MainMenu(final CnakesConfiguration configuration) {
		this.configuration = configuration;
		this.drawing = new Drawing(configuration);
		initConfiguration();
	}

	/**
	 * Cleans up (releases) the resources and destroys the window.
	 */
	private void cleanUp() {
		glfwFreeCallbacks(this.windowId);
		glfwDestroyWindow(this.windowId);
	}

	private void initConfiguration() {
		this.fullScreen = this.configuration.getVideo().getResolution().isFullScreen();
		this.screenWidth = this.configuration.getVideo().getResolution().getHorizontal();
		this.screenHeight = this.configuration.getVideo().getResolution().getVertical();
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

		final long fullScreenMonitor;
		if (this.fullScreen) {
			fullScreenMonitor = this.monitor == null ? glfwGetPrimaryMonitor() : glfwGetMonitors().get(this.monitor);
		} else {
			fullScreenMonitor = NULL;
		}


		this.windowId = glfwCreateWindow(this.screenWidth, this.screenHeight, "Cnakes", fullScreenMonitor, NULL);
		if (this.windowId == NULL) {
			throw new IllegalStateException("Failed to create the GLFW window");
		}

		// Get the resolution of the primary monitor
		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(this.windowId, (vidmode.width() - this.screenWidth) / 2, (vidmode.height() - this.screenHeight) / 2);

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
		initMenu();
		this.drawing.initFont("fonts/trs-million_rg.ttf");
		gameLoop();
		cleanUp();
	}

	/**
	 * Initializes the game.
	 */
	private void initMenu() {
		navigation = new MenuNavigation();
		glfwSetKeyCallback(this.windowId, new MenuKeyListener(navigation));
	}

	/**
	 * Starts the game loop, that keeps the game running.
	 */
	private void gameLoop() {
		while (!glfwWindowShouldClose(this.windowId)) {
			glClear(GL_COLOR_BUFFER_BIT);
			update();

			glfwSwapBuffers(this.windowId);
			try {
				glfwPollEvents();
			} catch (final NullPointerException e) {
				System.exit(0);
			}
		}

	}

	private void update() {
		final MenuItem[] menuItems = this.navigation.getMenuItems();
		final int itemCount = menuItems.length;
		for (int i = 0; i < itemCount; i++) {
			final MenuItem item = menuItems[i];

			glEnable(GL_TEXTURE_2D);
			this.drawing.drawOutlinedText(item.getName(), 3, 10,  25 , Color4f.YELLOW, Color4f.RED,true);
			glDisable(GL_TEXTURE_2D);
		}
	}
}
