package lv.herbis.cnakes.screens.help;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.context.ContextItems;
import lv.herbis.cnakes.draw.Drawing;
import lv.herbis.cnakes.listeners.HelpScreenControllerListener;
import lv.herbis.cnakes.listeners.HelpScreenKeyListener;
import lv.herbis.cnakes.screens.CnakesScreen;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class HelpScreen implements CnakesScreen {

	private static final String RULES = "Rules:";
	private static final String RULE_1 = "Step 1: Collect the bugs!";
	private static final String RULE_1_1 = "It will make you grow longer.";
	private static final String RULE_2 = "Step 2: Eat yourself!";
	private static final String RULE_2_2 = "Score points by eating yourself.";

	private static final String CONTROLS_MOVEMENT = "Movement:";
	private static final String CONTROLS_MOVEMENT_1 = "Use arrow keys or joystick to change directions.";

	private static final String CONTROLS_ACTIONS = "Actions:";
	private static final String CONTROLS_CONFIRM = "Confirm with 'Enter' key.";
	private static final String CONTROLS_BACK = "Go back with 'ESC' key.";
	private static final String CONTROLS_PAUSE = "Pause with 'SPACE' key.";


	private final ContextItems contextItems;

	private long windowId;
	private int screenWidthCenterScaled;
	private int screenHeightScaled;

	private Drawing drawing;

	public HelpScreen(final ContextItems contextItems) {
		this.contextItems = contextItems;
	}

	private void initConfiguration() {
		final CnakesConfiguration configuration = this.contextItems.getConfiguration();
		this.windowId = this.contextItems.getWindowId();
		this.drawing = this.contextItems.getDrawing();

		final int gameScale = configuration.getVideo().getScale();
		final int screenWidth = configuration.getVideo().getResolution().getHorizontal();
		final int screenHeight = configuration.getVideo().getResolution().getVertical();
		final int screenWidthScaled = screenWidth / gameScale;
		this.screenHeightScaled = screenHeight / gameScale;
		this.screenWidthCenterScaled = screenWidthScaled / 2;
	}

	@Override
	public void initScreen() {
		initConfiguration();
		glfwSetKeyCallback(this.windowId, new HelpScreenKeyListener());
		final HelpScreenControllerListener controllerListener = new HelpScreenControllerListener();
		glfwSetJoystickCallback(controllerListener);
		this.contextItems.getControllerStatePublisher().setControllerListener(controllerListener);
	}

	@Override
	public void update() {
		this.drawing.drawText(RULES, 2, this.screenWidthCenterScaled, this.screenHeightScaled - 2f, Color.GREEN, true);
		this.drawing.drawText(RULE_1, 1.5f, this.screenWidthCenterScaled, this.screenHeightScaled - 4.5f, Color.GREEN,
							  true);
		this.drawing.drawText(RULE_1_1, 1f, this.screenWidthCenterScaled, this.screenHeightScaled - 6.5f, Color.GREEN,
							  true);
		this.drawing
				.drawText(RULE_2, 1.5f, this.screenWidthCenterScaled, this.screenHeightScaled - 8f, Color.GREEN, true);
		this.drawing
				.drawText(RULE_2_2, 1f, this.screenWidthCenterScaled, this.screenHeightScaled - 10f, Color.GREEN, true);

		this.drawing.drawText(CONTROLS_MOVEMENT, 2f, this.screenWidthCenterScaled, this.screenHeightScaled - 13f,
							  Color.CYAN, true);
		this.drawing.drawText(CONTROLS_MOVEMENT_1, 1.5f, this.screenWidthCenterScaled, this.screenHeightScaled - 15.5f,
							  Color.CYAN, true);

		this.drawing.drawText(CONTROLS_ACTIONS, 2f, this.screenWidthCenterScaled, this.screenHeightScaled - 19f,
							  Color.YELLOW, true);
		this.drawing.drawText(CONTROLS_CONFIRM, 1.5f, this.screenWidthCenterScaled, this.screenHeightScaled - 21.5f,
							  Color.YELLOW, true);
		this.drawing.drawText(CONTROLS_BACK, 1.5f, this.screenWidthCenterScaled, this.screenHeightScaled - 23.5f,
							  Color.YELLOW, true);
		this.drawing.drawText(CONTROLS_PAUSE, 1.5f, this.screenWidthCenterScaled, this.screenHeightScaled - 25.5f,
							  Color.YELLOW, true);
	}
}
