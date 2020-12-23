package lv.herbis.cnakes.listeners;

import lv.herbis.cnakes.status.GameStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SinglePlayerScreenControllerListenerTest {

	@Mock
	private GameStatus mockGameStatus;

	private SinglePlayerScreenControllerListener listener;

	@Before
	public void setup() {
		listener = new SinglePlayerScreenControllerListener(mockGameStatus);
	}


	@Test
	public void isValidGameStateForMovementChangeWithRunningState() {
		doReturn(true).when(mockGameStatus).isBeingPlayed();
		doReturn(false).when(mockGameStatus).isPaused();
		doReturn(false).when(mockGameStatus).hasEnded();
		assertTrue("Game state should be valid for movement.", this.listener.isValidGameStateForMovementChange());
	}

	@Test
	public void isNotValidGameStateForMovementChangeNotStarted() {
		doReturn(false).when(mockGameStatus).isBeingPlayed();
		assertFalse("Game state should not be valid for movement when not started.",
					this.listener.isValidGameStateForMovementChange());
	}

	@Test
	public void isNotValidGameStateForMovementChangeHasEndedNotBeingPlayed() {
		doReturn(false).when(mockGameStatus).isBeingPlayed();
		assertFalse("Game state should not be valid for movement when ended and not being played..",
					this.listener.isValidGameStateForMovementChange());
	}

	@Test
	public void isNotValidGameStateForMovementChangeHasEndedAndBeingPlayed() {
		doReturn(true).when(mockGameStatus).isBeingPlayed();
		doReturn(true).when(mockGameStatus).hasEnded();
		assertFalse("Game state should not be valid for movement when ended and being played.",
					this.listener.isValidGameStateForMovementChange());
	}

	@Test
	public void isValidGameStateForMovementChangeWhenPaused() {
		doReturn(true).when(mockGameStatus).isBeingPlayed();
		doReturn(true).when(mockGameStatus).isPaused();
		assertFalse("Game state should not be valid for movement when paused.",
					this.listener.isValidGameStateForMovementChange());
	}
}