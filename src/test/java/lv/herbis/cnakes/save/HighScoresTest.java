package lv.herbis.cnakes.save;

import org.junit.Test;

import static org.junit.Assert.*;

public class HighScoresTest {

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
	public void getLimit() {
	}

	@Test
	public void changeLimit() {
	}

	@Test
	public void getHighScoreList() {
	}

	@Test
	public void getTopScore() {
	}

	@Test
	public void getBottomScore() {
	}

	@Test
	public void getHighScoreCount() {
	}

	@Test
	public void hasHighScores() {
	}
}