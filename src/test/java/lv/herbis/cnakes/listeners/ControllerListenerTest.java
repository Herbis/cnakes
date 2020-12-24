package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.controls.AxisDirection;
import lv.herbis.cnakes.controls.ControllerMapping;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

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
	public void determineAxisDirectionWhenNone()
	{
		assertEquals("Direction Should be NONE.", AxisDirection.NONE, ControllerListener.determineAxisDirection(AxisDirection.NONE, AxisDirection.NONE, AxisDirection.NONE));
	}

	@Test
	public void determineAxisDirectionWhenHorizontalPreviousAndHorizontalChange()
	{
		assertEquals("L1: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.NONE, AxisDirection.LEFT));
		assertEquals("L2: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.NONE, AxisDirection.RIGHT));
		assertEquals("R1: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.NONE, AxisDirection.LEFT));
		assertEquals("R2: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.NONE, AxisDirection.RIGHT));
	}

	@Test
	public void determineAxisDirectionWhenNonePreviousAndHorizontalChange()
	{
		assertEquals("L3: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.NONE, AxisDirection.NONE));
		assertEquals("R3: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.NONE, AxisDirection.NONE));
	}

	@Test
	public void determineAxisDirectionWhenPreviousHorizontalMatchAndBothChange()
	{
		assertEquals("L4: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.UP, AxisDirection.LEFT));
		assertEquals("R4: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.UP, AxisDirection.RIGHT));
		assertEquals("L5: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.DOWN, AxisDirection.LEFT));
		assertEquals("R5: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.DOWN, AxisDirection.RIGHT));
	}

	@Test
	public void determineAxisDirectionWhenPreviousNoneAndBothChange()
	{
		assertEquals("V1: Direction Should be UP because vertical is prioritized.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.UP, AxisDirection.NONE));
		assertEquals("V2: Direction Should be UP because vertical is prioritized.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.UP, AxisDirection.NONE));
		assertEquals("V3: Direction Should be DOWN because vertical is prioritized.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.DOWN, AxisDirection.NONE));
		assertEquals("V4: Direction Should be DOWN because vertical is prioritized.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.DOWN, AxisDirection.NONE));
	}

	@Test
	public void determineAxisDirectionWhenPreviousHorizontalOppositeAndBothChanged()
	{
		assertEquals("OH1: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.UP, AxisDirection.RIGHT));
		assertEquals("OH2: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.UP, AxisDirection.LEFT));
		assertEquals("OH3: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.DOWN, AxisDirection.RIGHT));
		assertEquals("OH4: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.DOWN, AxisDirection.LEFT));
	}

	@Test
	public void determineAxisDirectionWhenPreviousVerticalOppositeAndBothChanged()
	{
		assertEquals("OV1: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.UP, AxisDirection.DOWN));
		assertEquals("OV2: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.UP, AxisDirection.DOWN));
		assertEquals("OV3: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.DOWN, AxisDirection.UP));
		assertEquals("OV4: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.DOWN, AxisDirection.UP));
	}

	@Test
	public void determineAxisDirectionWhenVerticalPreviousAndVerticalChange()
	{
		assertEquals("U1: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.UP, AxisDirection.NONE, AxisDirection.UP));
		assertEquals("U2: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.UP, AxisDirection.NONE, AxisDirection.DOWN));
		assertEquals("D1: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.DOWN, AxisDirection.NONE, AxisDirection.UP));
		assertEquals("D2: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.DOWN, AxisDirection.NONE, AxisDirection.DOWN));
	}

	@Test
	public void determineAxisDirectionWhenNonePreviousAndVerticalChange()
	{
		assertEquals("U3: Direction Should be UP.", AxisDirection.UP, ControllerListener.determineAxisDirection(AxisDirection.UP, AxisDirection.NONE, AxisDirection.NONE));
		assertEquals("D3: Direction Should be DOWN.", AxisDirection.DOWN, ControllerListener.determineAxisDirection(AxisDirection.DOWN, AxisDirection.NONE, AxisDirection.NONE));
	}

	@Test
	public void determineAxisDirectionWhenPreviousVerticalMatchAndBothChange()
	{
		assertEquals("U4: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.UP, AxisDirection.UP));
		assertEquals("D4: Direction Should be LEFT.", AxisDirection.LEFT, ControllerListener.determineAxisDirection(AxisDirection.LEFT, AxisDirection.DOWN, AxisDirection.DOWN));
		assertEquals("U5: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.UP, AxisDirection.UP));
		assertEquals("D5: Direction Should be RIGHT.", AxisDirection.RIGHT, ControllerListener.determineAxisDirection(AxisDirection.RIGHT, AxisDirection.DOWN, AxisDirection.DOWN));
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
