package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ControllerMapping;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ControllerListenerTest {

	private static final long DPAD_DIFFERENCE_UNDER = ControllerListener.MIN_DPAD_TIME_DIFFERENCE - 5_000_000;
	private static final long DPAD_DIFFERENCE_OVER = ControllerListener.MIN_DPAD_TIME_DIFFERENCE + 5_000_000;
	private ControllerListener controllerListener;

	@Before
	public void setup() {
		controllerListener = Mockito.mock(ControllerListener.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	public void isHorizontalDirectionNone() {
		assertFalse("NONE direction should not be a horizontal direction.", ControllerListener.isHorizontalDirection(AxisDirection.NONE));

	}

	@Test
	public void isHorizontalDirectionNull() {
		assertFalse("Null direction should not be a horizontal direction.", ControllerListener.isHorizontalDirection(null));
	}

	@Test
	public void isHorizontalDirectionLeft() {
		assertTrue("Left direction should be a horizontal direction.", ControllerListener.isHorizontalDirection(AxisDirection.LEFT));
	}

	@Test
	public void isHorizontalDirectionRight() {
		assertTrue("Right direction should be a horizontal direction.", ControllerListener.isHorizontalDirection(AxisDirection.RIGHT));
	}

	@Test
	public void isHorizontalDirectionUp() {
		assertFalse("Up direction should not be a horizontal direction.", ControllerListener.isHorizontalDirection(AxisDirection.UP));
	}

	@Test
	public void isHorizontalDirectionDown() {
		assertFalse("Down direction should not be a horizontal direction.", ControllerListener.isHorizontalDirection(AxisDirection.DOWN));
	}

	@Test
	public void isVerticalDirectionNone() {
		assertFalse("NONE direction should not be a vertical direction.", ControllerListener.isVerticalDirection(AxisDirection.NONE));
	}

	@Test
	public void isVerticalDirectionNull() {
		assertFalse("Null direction should not be a vertical direction.", ControllerListener.isVerticalDirection(null));
	}

	@Test
	public void isVerticalDirectionLeft() {
		assertFalse("Left direction should not be a vertical direction.", ControllerListener.isVerticalDirection(AxisDirection.LEFT));
	}

	@Test
	public void isVerticalDirectionRight() {
		assertFalse("Right direction should not be a vertical direction.", ControllerListener.isVerticalDirection(AxisDirection.RIGHT));
	}

	@Test
	public void isVerticalDirectionUp() {
		assertTrue("Up direction should be a vertical direction.", ControllerListener.isVerticalDirection(AxisDirection.UP));
	}

	@Test
	public void isVerticalDirectionDown() {
		assertTrue("Down direction should be a vertical direction.", ControllerListener.isVerticalDirection(AxisDirection.DOWN));
	}

	@Test
	public void isDirectionalPadButtonWithAllDirections() {
		final ControllerMapping controllerMapping = new ControllerMapping();
		final int upDir = 0;
		final int downDir = 1;
		final int leftDir = 2;
		final int rightDir = 3;

		controllerMapping.setUp(upDir);
		controllerMapping.setDown(downDir);
		controllerMapping.setLeft(leftDir);
		controllerMapping.setRight(rightDir);

		assertTrue("Up direction should be recognized.", ControllerListener.isDirectionalPadButton(upDir, controllerMapping));
		assertTrue("Down direction should be recognized.", ControllerListener.isDirectionalPadButton(downDir, controllerMapping));
		assertTrue("Left direction should be recognized.", ControllerListener.isDirectionalPadButton(leftDir, controllerMapping));
		assertTrue("Right direction should be recognized.", ControllerListener.isDirectionalPadButton(rightDir, controllerMapping));
	}

	@Test
	public void isDirectionalPadButtonWithNoDirections() {
		final ControllerMapping controllerMapping = new ControllerMapping();
		final int upDir = 0;
		final int downDir = 1;
		final int rndButton = 5;

		controllerMapping.setUp(upDir);
		controllerMapping.setDown(downDir);
		controllerMapping.setConfirm(rndButton);
		controllerMapping.setCancel(rndButton);
		controllerMapping.setBack(rndButton);
		controllerMapping.setStart(rndButton);

		assertFalse("No direction should be recognized.", ControllerListener.isDirectionalPadButton(rndButton, controllerMapping));
	}

	@Test
	public void checkDpadChangeRealisticNotWhenUnderMinDifLastSuccessful() {
		final boolean isChangeRealistic = ControllerListener.checkDpadChangeRealistic(true, System.nanoTime() - DPAD_DIFFERENCE_UNDER, Strings.EMPTY);
		assertFalse("Change should not be realistic under min difference, when last move successful.", isChangeRealistic);
	}

	@Test
	public void checkDpadChangeRealisticWhenUnderMinDifLastUnsuccessful() {
		final boolean isChangeRealistic = ControllerListener.checkDpadChangeRealistic(false, System.nanoTime() - DPAD_DIFFERENCE_UNDER, Strings.EMPTY);
		assertTrue("Change should be realistic under min difference, when last move successful.", isChangeRealistic);
	}

	@Test
	public void checkDpadChangeRealisticWhenOverMinDifLastUnsuccessful() {
		final boolean isChangeRealistic = ControllerListener.checkDpadChangeRealistic(false, System.nanoTime() - DPAD_DIFFERENCE_OVER, Strings.EMPTY);
		assertTrue("Change should be realistic over min difference, when last move successful.", isChangeRealistic);
	}

	@Test
	public void checkDpadChangeRealisticWhenOverMinDifLastSuccessful() {
		final boolean isChangeRealistic = ControllerListener.checkDpadChangeRealistic(true, System.nanoTime() - DPAD_DIFFERENCE_OVER, Strings.EMPTY);
		assertTrue("Change should be realistic over min difference, when last move successful.", isChangeRealistic);
	}
}
