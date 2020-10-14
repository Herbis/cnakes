package lv.herbis.cnakes.controls;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ControllerStatePublisherTest {

	private final float VALID_AXIS_POS_VALUE = 0.9f;
	private final float VALID_AXIS_NEG_VALUE = -0.9f;

	private ControllerStatePublisher controllerStatePublisher;

	@Before
	public void setUp() {
		controllerStatePublisher = new ControllerStatePublisher();
		final Thread controllerStatePublisherThread = new Thread(controllerStatePublisher);
		controllerStatePublisherThread.start();
	}

	@After
	public void tearDown(){
		ControllerStatePublisher.stop();
	}

	@Test
	public void testIsAxisValueDifferentEnoughNoPreviousAxisState() {
		assertFalse("If no previous axis state has been given, there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(0, VALID_AXIS_POS_VALUE, null));
	}

	@Test
	public void testIsAxisValueDifferentEnoughPreviousAxisStateLengthSmallerThanAxisId() {
		final float[] previousState = new float[2];
		previousState[0] = 0;
		previousState[1] = 0;

		assertFalse("If previous axis state [] length is smaller than axis id, there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(2, VALID_AXIS_POS_VALUE, previousState));
	}

	@Test
	public void isAxisValueDifferentEnoughMatchesPreviousValue() {
		final float[] previousState = new float[2];
		previousState[0] = VALID_AXIS_POS_VALUE;
		previousState[1] = 0;

		assertFalse("If previous axis state value matches new value, there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(0, VALID_AXIS_POS_VALUE, previousState));
	}

	@Test
	public void isAxisValueDifferentEnoughChangeSmallerThanMinValueChange() {
		final float[] previousState = new float[2];
		previousState[0] = VALID_AXIS_POS_VALUE;
		previousState[1] = 0;

		final float newValue = VALID_AXIS_POS_VALUE - ControllerStatePublisher.AXIS_MIN_VALUE_CHANGE + 0.002f;

		assertFalse("If previous axis state value is less different than required min value change, " +
							"there should be no difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(0, newValue, previousState));
	}

	@Test
	public void isAxisValueDifferentEnoughChangeLargerThanMinValueChange() {
		final float[] previousState = new float[2];
		previousState[0] = VALID_AXIS_POS_VALUE;
		previousState[1] = VALID_AXIS_NEG_VALUE;

		final float newValue = VALID_AXIS_POS_VALUE - ControllerStatePublisher.AXIS_MIN_VALUE_CHANGE - 0.002f;
		final float newNegValue = VALID_AXIS_POS_VALUE + ControllerStatePublisher.AXIS_MIN_VALUE_CHANGE + 0.002f;

		assertTrue("If previous axis state value is more different than required min value change, " +
							"there should be difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(0, newValue, previousState));
		assertTrue("If previous axis state value is more different than required min value change, " +
							"there should be difference.",
					ControllerStatePublisher.isAxisValueDifferentEnough(1, newNegValue, previousState));

	}

}