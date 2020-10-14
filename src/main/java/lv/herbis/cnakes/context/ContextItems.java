package lv.herbis.cnakes.context;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.controls.ControllerStatePublisher;
import lv.herbis.cnakes.draw.Drawing;
import lv.herbis.cnakes.movement.DefaultMenuNavigation;
import lv.herbis.cnakes.movement.MenuNavigation;
import lv.herbis.cnakes.sound.SoundManager;

public class ContextItems {
	private CnakesConfiguration configuration;
	private long windowId;
	private SoundManager soundManager;
	private ControllerStatePublisher controllerStatePublisher;
	private Drawing drawing;
	private MenuNavigation menuNavigation;
	private Thread controllerStatePublisherThread;

	public void cleanup() {
		if (this.controllerStatePublisher != null) {
			this.controllerStatePublisher.stop();
		}

		this.soundManager.cleanup();
	}

	public void initControllerStatePublisher() {
		if (this.controllerStatePublisherThread == null || this.controllerStatePublisher == null) {
			if (this.controllerStatePublisherThread != null && this.controllerStatePublisherThread.isAlive()) {
				this.controllerStatePublisherThread.interrupt();
			}

			this.controllerStatePublisher = new ControllerStatePublisher(null, getMenuNavigation());
			this.controllerStatePublisherThread = new Thread(this.controllerStatePublisher);
			this.controllerStatePublisherThread.start();
		} else {
			this.controllerStatePublisher.setMenuNavigation(getMenuNavigation());
		}
	}

	public CnakesConfiguration getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(final CnakesConfiguration configuration) {
		this.configuration = configuration;
	}

	public long getWindowId() {
		return this.windowId;
	}

	public void setWindowId(final long windowId) {
		this.windowId = windowId;
	}

	public SoundManager getSoundManager() {
		if (this.soundManager == null) {
			this.soundManager = new SoundManager();
		}
		return this.soundManager;
	}

	public void setSoundManager(final SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	public ControllerStatePublisher getControllerStatePublisher() {
		if (this.controllerStatePublisher == null) {
			initControllerStatePublisher();
		}

		return this.controllerStatePublisher;
	}

	public void setControllerStatePublisher(final ControllerStatePublisher controllerStatePublisher) {
		this.controllerStatePublisher = controllerStatePublisher;
	}


	public Drawing getDrawing() {
		if (this.drawing == null) {
			this.drawing = new Drawing(getConfiguration());
		}

		return this.drawing;
	}

	public void setDrawing(final Drawing drawing) {
		this.drawing = drawing;
	}

	public MenuNavigation getMenuNavigation() {
		if (this.menuNavigation == null) {
			this.menuNavigation = new DefaultMenuNavigation(this);
		}

		return this.menuNavigation;
	}

	public void setMenuNavigation(final MenuNavigation menuNavigation) {
		this.menuNavigation = menuNavigation;
	}
}
