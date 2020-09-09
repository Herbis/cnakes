package lv.herbis.cnakes.save;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class HighScoresTest {

	private static final int TOP_SCORE = 500;
	private static final int BOTTOM_SCORE = 10;

	@Test
	public void addHighScoreWithinLimit() {
		final HighScores highScores = new HighScores(5);
		final HighScore highScore1 = new HighScore("test1", 20);
		final HighScore highScore2 = new HighScore("test2", 15);
		highScores.addHighScore(highScore1);
		highScores.addHighScore(highScore2);

		assertNotNull("High scores should not be null.", highScores.getHighScoreList());
		assertEquals("Two high scores should be present.", 2, highScores.getHighScoreList().size());
		assertTrue("HighScores should contain our first added score.", highScores.getHighScoreList().contains(highScore1));
		assertTrue("HighScores should contain our second added score.", highScores.getHighScoreList().contains(highScore2));
	}

	@Test
	public void addHighScoreOutsideLimit() {
		final HighScores highScores = new HighScores(2);
		final HighScore highScore1 = new HighScore("test1", 20);
		final HighScore highScore2 = new HighScore("test2", 15);
		final HighScore highScore3 = new HighScore("test3", 25);
		final HighScore highScore4 = new HighScore("test4", 10);
		highScores.addHighScore(highScore1);
		highScores.addHighScore(highScore2);
		highScores.addHighScore(highScore3);
		highScores.addHighScore(highScore4);

		assertNotNull("High scores should not be null.", highScores.getHighScoreList());
		assertEquals("Two high scores should be present.", 2, highScores.getHighScoreList().size());
		assertTrue("HighScores should contain our first added score.", highScores.getHighScoreList().contains(highScore1));
		assertFalse("HighScores should not contain our second added score.", highScores.getHighScoreList().contains(highScore2));
		assertTrue("HighScores should contain our third added score.", highScores.getHighScoreList().contains(highScore3));
		assertFalse("HighScores should not contain our forth added score.", highScores.getHighScoreList().contains(highScore4));
	}

	@Test
	public void addHighScoreOfZero() {
		final HighScores highScores = new HighScores(2);
		final HighScore highScore1 = new HighScore("test1", 20);
		final HighScore highScore2 = new HighScore("test2", 0);
		highScores.addHighScore(highScore1);
		highScores.addHighScore(highScore2);

		assertNotNull("High scores should not be null.", highScores.getHighScoreList());
		assertEquals("One high score should be present.", 1, highScores.getHighScoreList().size());
		assertTrue("HighScores should contain our first added score.", highScores.getHighScoreList().contains(highScore1));
		assertFalse("HighScores should not contain our second added score.", highScores.getHighScoreList().contains(highScore2));
	}

	@Test
	public void getLimitWhenInitializedWithValidNumber() {
		final int validLimit = 200;

		final HighScores highScores = new HighScores(validLimit);
		assertEquals("Limit should be same as it was set.", validLimit, highScores.getLimit());
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructWithNegativeLimit() {
		final int negativeLimit = -5;
		new HighScores(negativeLimit);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructWithZeroLimit() {
		final int zeroLimit = 0;
		new HighScores(zeroLimit);
	}

	@Test
	public void changeLimitWithValidNumber() {
		final int initialLimit = 200;
		final int newLimit = initialLimit + 20;

		final HighScores highScores = new HighScores(initialLimit);
		highScores.changeLimit(newLimit);
		assertEquals("Limit should be changed to the new limit.", newLimit, highScores.getLimit());
	}

	@Test(expected = IllegalArgumentException.class)
	public void changeLimitWithNegativeNumber() {
		final HighScores highScores = new HighScores(200);
		highScores.changeLimit(-5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void changeLimitWithZeroNumber() {
		final HighScores highScores = new HighScores(200);
		highScores.changeLimit(0);
	}


	@Test
	public void getHighScoreListWhenNoEntriesAdded() {
		final HighScores highScores = new HighScores(15);
		final List<HighScore> highScoreList = highScores.getHighScoreList();
		assertNotNull("High score list should not be null.", highScoreList);
		assertTrue("High score list should be empty.", highScoreList.isEmpty());
	}

	@Test
	public void getHighScoreListWithEntries() {
		final HighScores highScores = new HighScores(15);
		highScores.addHighScore(new HighScore("test", 15));
		final List<HighScore> highScoreList = highScores.getHighScoreList();
		assertNotNull("High score list should not be null.", highScoreList);
		assertFalse("High score list should not be empty.", highScoreList.isEmpty());
	}

	@Test
	public void getTopScoreWithMixedEntries() {
		final HighScores highScores = getHighScoresWithMixedEntries();
		final HighScore topScore = highScores.getTopScore();

		assertNotNull("Top score should not be null.", topScore);
		assertEquals("Top score should match the highest score.", TOP_SCORE, topScore.getScore());
	}

	@Test
	public void getTopScoreWithNoEntries() {
		final HighScores highScores = new HighScores();
		assertNull("Top score should not exist.", highScores.getTopScore());
	}

	@Test
	public void getBottomScoreWithMixedEntries() {
		final HighScores highScores = getHighScoresWithMixedEntries();
		final HighScore bottomScore = highScores.getBottomScore();

		assertNotNull("Bottom score should not be null.", bottomScore);
		assertEquals("Bottom score should match the highest score.", BOTTOM_SCORE, bottomScore.getScore());

	}

	@Test
	public void getBottomScoreWithNoEntries() {
		final HighScores highScores = new HighScores();
		assertNull("Bottom score should not exist.", highScores.getBottomScore());
	}

	private HighScores getHighScoresWithMixedEntries()
	{
		final HighScores highScores = new HighScores(20);
		final HighScore highScore1 = new HighScore("test1", BOTTOM_SCORE);
		final HighScore highScore2 = new HighScore("test2", TOP_SCORE - 20);
		final HighScore highScore3 = new HighScore("test3", TOP_SCORE);
		final HighScore highScore4 = new HighScore("test4", TOP_SCORE - 10);
		final HighScore highScore5 = new HighScore("test5", TOP_SCORE - 30);

		highScores.addHighScore(highScore1);
		highScores.addHighScore(highScore2);
		highScores.addHighScore(highScore3);
		highScores.addHighScore(highScore4);
		highScores.addHighScore(highScore5);

		return highScores;
	}

	@Test
	public void getHighScoreCountWithNoEntries() {
		final HighScores highScores = new HighScores(20);
		assertEquals("", 0, highScores.getHighScoreCount());
	}

	@Test
	public void getHighScoreCountWhenSomeNotAdded() {
		final HighScores highScores = new HighScores(3);
		final HighScore highScore1 = new HighScore("test1", 5);
		final HighScore highScore2 = new HighScore("test2", 0);
		final HighScore highScore3 = new HighScore("test3", 35);
		final HighScore highScore4 = new HighScore("test4", 20);
		final HighScore highScore5 = new HighScore("test5", 10);

		highScores.addHighScore(highScore1);
		assertEquals("Should have only one high score at this point.", 1, highScores.getHighScoreCount());

		highScores.addHighScore(highScore2);
		assertEquals("Should still have only one high score at this point.", 1, highScores.getHighScoreCount());

		highScores.addHighScore(highScore3);
		highScores.addHighScore(highScore4);
		highScores.addHighScore(highScore5);

		assertEquals("Should have three high scores at this point.", 3, highScores.getHighScoreCount());
	}

	@Test
	public void hasHighScoresWithNoScores() {
		final HighScores highScores = new HighScores(3);
		assertFalse("There should be no high scores.", highScores.hasHighScores());
	}

	@Test
	public void hasHighScoresWithMixedScores() {
		final HighScores highScores = new HighScores(3);
		final HighScore highScore1 = new HighScore("test1", 5);
		final HighScore highScore2 = new HighScore("test2", 5);

		highScores.addHighScore(highScore1);

		assertTrue("There should be high scores at this point.", highScores.hasHighScores());

		highScores.addHighScore(highScore2);
		assertTrue("There should still be high scores at this point.", highScores.hasHighScores());
	}
}