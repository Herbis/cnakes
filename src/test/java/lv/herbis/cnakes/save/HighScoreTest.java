package lv.herbis.cnakes.save;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HighScoreTest {

	private static final int VALID_SCORE = 21;
	private static final int ANOTHER_VALID_SCORE = 138;
	private static final int INVALID_SCORE = -21;
	private static final int ZERO_SCORE = 0;

	private static final long VALID_TIMESTAMP = 12315231651321L;
	private static final long INVALID_TIMESTAMP = -123543121;

	private static final String TEST_USER_1 = "Test user 1";

	@Test
	public void setValidUserAndScoreInConstructor() {
		final HighScore highScore = new HighScore(TEST_USER_1, VALID_SCORE);

		assertEquals("Score should be the same as passed to constructor.", VALID_SCORE, highScore.getScore());
		assertEquals("Username should be the same as passed to constructor.", TEST_USER_1, highScore.getUsername());
	}

	@Test
	public void setValidUserAndScoreAndTimestampInConstructor() {
		final long timestamp = Calendar.getInstance().getTimeInMillis();
		final HighScore highScore = new HighScore(TEST_USER_1, VALID_SCORE, timestamp);

		assertEquals("Score should be the same as passed to constructor.", VALID_SCORE, highScore.getScore());
		assertEquals("Username should be the same as passed to constructor.", TEST_USER_1, highScore.getUsername());
		assertEquals("Timestamp should be the same as passed to constructor.", timestamp, highScore.getTimestamp());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setInvalidScore() {
		final HighScore highScore = createValidHighScore();
		highScore.setScore(INVALID_SCORE);
	}

	@Test
	public void setValidScore() {
		final HighScore highScore = createValidHighScore();
		highScore.setScore(ANOTHER_VALID_SCORE);

		assertEquals("Set score should be the same.", ANOTHER_VALID_SCORE, highScore.getScore());
	}

	@Test
	public void setZeroScore() {
		final HighScore highScore = createValidHighScore();
		highScore.setScore(ZERO_SCORE);

		assertEquals("Set score should be 0.", ZERO_SCORE, highScore.getScore());
	}

	@Test
	public void setValidTimestamp() {
		final HighScore highScore = createValidHighScore();
		highScore.setTimestamp(VALID_TIMESTAMP);

		assertEquals("Timestamp should be the same as set.", VALID_TIMESTAMP, highScore.getTimestamp());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setInvalidTimestamp() {
		final HighScore highScore = createValidHighScore();
		highScore.setTimestamp(INVALID_TIMESTAMP);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setZeroTimestamp() {
		final HighScore highScore = createValidHighScore();
		highScore.setTimestamp(0);
	}

	@Test
	public void setValidUsername() {
		final String testUser = TEST_USER_1 + "_5";
		final HighScore highScore = createValidHighScore();
		highScore.setUsername(testUser);
		assertEquals("Username should be the same as set.", testUser, highScore.getUsername());
	}

	@Test
	public void setEmptyUsername() {
		final String emptyUsername = "";
		final String blankUsername = "    ";
		final HighScore highScore = createValidHighScore();
		highScore.setUsername(emptyUsername);
		assertEquals("Empty username should remain the same.", emptyUsername, highScore.getUsername());

		highScore.setUsername(blankUsername);
		assertEquals("Blank username should remain the same.", blankUsername, highScore.getUsername());
	}

	@Test(expected = NullPointerException.class)
	public void setNullUsername() {
		final HighScore highScore = createValidHighScore();
		highScore.setUsername(null);
	}

	@Test
	public void testSameHighScoreEquals() {
		final HighScore highScore = createValidHighScore();
		final HighScore sameHighScore = createValidHighScore();
		assertEquals("Same reference high scores should equal.", highScore, highScore);
		assertEquals("Same value high scores should equal.", highScore, sameHighScore);
	}

	@Test
	public void testDifferentHighScoreEquals() {
		final HighScore highScoreInitial = new HighScore("Test 1", 20, 10);
		final HighScore highScoreDiffName = new HighScore("Test 2", 20, 10);
		final HighScore highScoreDiffScore = new HighScore("Test 1", 22, 10);
		final HighScore highScoreDiffTimestamp = new HighScore("Test 1", 20, 15);

		assertNotEquals("High scores with different usernames should not equal.", highScoreInitial, highScoreDiffName);
		assertNotEquals("High scores with different scores should not equal.", highScoreInitial, highScoreDiffScore);
		assertNotEquals("High scores with different timestamps should not equal.", highScoreInitial,
						highScoreDiffTimestamp);
		assertNotEquals("Initial High score should not be equal to null.", highScoreInitial, null);
		assertNotEquals("Initial High score should not be equal to random Object.", highScoreInitial, new Object());
	}


	@Test
	public void testSameHighScoreHashCode() {
		final HighScore highScore = createValidHighScore();
		final int sameHighScore = createValidHighScore().hashCode();
		assertEquals("Same reference high scores should have same hash codes.", createValidHighScore().hashCode(),
					 createValidHighScore().hashCode());
		assertEquals("Same value high scores should have same hash codes.", highScore.hashCode(), sameHighScore);
	}

	@Test
	public void testDifferentHighScoreHashCodes() {
		final int highScoreInitial = new HighScore("Test 1", 20, 10).hashCode();
		final int highScoreDiffName = new HighScore("Test 2", 20, 10).hashCode();
		final int highScoreDiffScore = new HighScore("Test 1", 22, 10).hashCode();
		final int highScoreDiffTimestamp = new HighScore("Test 1", 20, 15).hashCode();

		assertNotEquals("High scores with different usernames should have different hash codes.", highScoreInitial,
						highScoreDiffName);
		assertNotEquals("High scores with different scores should have different hash codes.", highScoreInitial,
						highScoreDiffScore);
		assertNotEquals("High scores with different timestamps should have different hash codes.", highScoreInitial,
						highScoreDiffTimestamp);
	}

	private HighScore createValidHighScore() {
		return new HighScore(TEST_USER_1, VALID_SCORE, VALID_TIMESTAMP);
	}
}