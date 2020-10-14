package lv.herbis.cnakes.controls;

import lv.herbis.cnakes.listeners.ControllerListener;
import lv.herbis.cnakes.menus.MenuItem;
import lv.herbis.cnakes.movement.MenuNavigation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControllerStatePublisherTest {

	private final float VALID_AXIS_POS_VALUE = 0.9f;
	private final float VALID_AXIS_NEG_VALUE = -0.9f;

	private ControllerStatePublisher controllerStatePublisher;
	private Thread controllerStatePublisherThread;

	@Before
	public void setUp() {
		this.controllerStatePublisher = new ControllerStatePublisher(getMockControllerListener(),
																	 getMockMenuNavigation());
		this.controllerStatePublisherThread = new Thread(this.controllerStatePublisher);
		this.controllerStatePublisherThread.start();
	}

	private ControllerListener getMockControllerListener() {
		return new ControllerListener() {
			@Override
			public void processP1ControllerStateChange(final int buttonId, final ButtonState state) {

			}

			@Override
			protected void moveP1BasedOnCurrentAndPreviousAxisDirection(final AxisDirection direction,
																		final AxisDirection previousDirection) {

			}
		};
	}

	private MenuNavigation getMockMenuNavigation() {
		return new MenuNavigation() {
			@Override
			public void enterSelectedItem() {

			}

			@Override
			public void moveDown() {

			}

			@Override
			public void moveUp() {

			}

			@Override
			public void moveRight() {

			}

			@Override
			public void moveLeft() {

			}

			@Override
			public MenuItem[] getMenuItems() {
				return new MenuItem[0];
			}

			@Override
			public Object usePendingItem() {
				return null;
			}

			@Override
			public void setPendingItem(final Object pendingItem) {

			}

			@Override
			public MenuItem getActiveItem() {
				return null;
			}

			@Override
			public void setActiveItem(final MenuItem activeItem) {

			}
		};
	}

	@After
	public void tearDown() {
		this.controllerStatePublisher.stop();
	}

	@Test
	public void testIsAxisValueDifferentEnoughNoPreviousAxisState() {
		assertFalse("If no previous axis state has been given, there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(0, this.VALID_AXIS_POS_VALUE, null));
	}

	@Test
	public void testIsAxisValueDifferentEnoughPreviousAxisStateLengthSmallerThanAxisId() {
		final float[] previousState = new float[2];
		previousState[0] = 0;
		previousState[1] = 0;

		assertFalse("If previous axis state [] length is smaller than axis id, there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(2, this.VALID_AXIS_POS_VALUE, previousState));
	}

	@Test
	public void isAxisValueDifferentEnoughMatchesPreviousValue() {
		final float[] previousState = new float[2];
		previousState[0] = this.VALID_AXIS_POS_VALUE;
		previousState[1] = 0;

		assertFalse("If previous axis state value matches new value, there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(0, this.VALID_AXIS_POS_VALUE, previousState));
	}

	@Test
	public void isAxisValueDifferentEnoughChangeSmallerThanMinValueChange() {
		final float[] previousState = new float[2];
		previousState[0] = this.VALID_AXIS_POS_VALUE;
		previousState[1] = 0;

		final float newValue = this.VALID_AXIS_POS_VALUE - ControllerStatePublisher.AXIS_MIN_VALUE_CHANGE + 0.002f;

		assertFalse(
				"If previous axis state value is less different than required min value change, " + "there should be no difference.",
				ControllerStatePublisher.isAxisValueDifferentEnough(0, newValue, previousState));
	}

	@Test
	public void isAxisValueDifferentEnoughChangeLargerThanMinValueChange() {
		final float[] previousState = new float[2];
		previousState[0] = this.VALID_AXIS_POS_VALUE;
		previousState[1] = this.VALID_AXIS_NEG_VALUE;

		final float newValue = this.VALID_AXIS_POS_VALUE - ControllerStatePublisher.AXIS_MIN_VALUE_CHANGE - 0.002f;
		final float newNegValue = this.VALID_AXIS_POS_VALUE + ControllerStatePublisher.AXIS_MIN_VALUE_CHANGE + 0.002f;

		assertTrue(
				"If previous axis state value is more different than required min value change, " + "there should be difference.",
				ControllerStatePublisher.isAxisValueDifferentEnough(0, newValue, previousState));
		assertTrue(
				"If previous axis state value is more different than required min value change, " + "there should be difference.",
				ControllerStatePublisher.isAxisValueDifferentEnough(1, newNegValue, previousState));

	}

	@Test
	public void isButtonStateDifferentWithNoPreviousState() {
		assertFalse("If no previous button state has been given, there should be no difference.",
					ControllerStatePublisher.isButtonStateDifferent(0, (byte) 1, null));
	}

	@Test
	public void isButtonStateDifferentWithPreviousStateLengthBeingLowerThanButtonId() {
		final byte[] previousState = new byte[1];
		assertFalse("If previous button state [] length is smaller than btn id, there should be no difference.",
					ControllerStatePublisher.isButtonStateDifferent(2, (byte) 1, previousState));
	}

	@Test
	public void isButtonStateDifferentWithPreviousStateHavingSameValue() {
		final byte[] previousState = new byte[2];
		previousState[0] = 1;

		assertFalse("If previous button state has the same value for btn 0, there should be no difference.",
					ControllerStatePublisher.isButtonStateDifferent(0, (byte) 1, previousState));
		assertFalse("If previous button state has the same value for btn 1, there should be no difference.",
					ControllerStatePublisher.isButtonStateDifferent(1, (byte) 0, previousState));

	}

	@Test
	public void isButtonStateDifferentWithPreviousStateHavingDifferentValue() {
		final byte[] previousState = new byte[3];
		previousState[0] = 2;
		previousState[1] = 3;

		assertTrue("If previous button state has different value for btn 0, there should be difference.",
				   ControllerStatePublisher.isButtonStateDifferent(0, (byte) 1, previousState));
		assertTrue("If previous button state has different value for btn 1, there should be difference.",
				   ControllerStatePublisher.isButtonStateDifferent(1, (byte) 0, previousState));
		assertFalse("If previous button state has same value for btn 2, there should be no difference.",
					ControllerStatePublisher.isButtonStateDifferent(1, (byte) 3, previousState));
	}

	@Test
	public void testStopThread() throws InterruptedException {
		assertTrue("Publisher Thread should be alive at this point.", this.controllerStatePublisherThread.isAlive());
		this.controllerStatePublisher.stop();
		await().atLeast(100, TimeUnit.MILLISECONDS)
				.untilAsserted(() -> assertFalse(this.controllerStatePublisherThread.isAlive()));
	}
}