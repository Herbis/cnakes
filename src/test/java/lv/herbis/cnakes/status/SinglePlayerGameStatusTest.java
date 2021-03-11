package lv.herbis.cnakes.status;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

public class SinglePlayerGameStatusTest {

	SinglePlayerGameStatus gameStatus = new SinglePlayerGameStatus(new CnakesConfiguration(), 1) {
		@Override
		public void afterEnd() {
			// do nothing
		}

		@Override
		public void submitHighScore() {
			// do nothing
		}
	};

	@Test
	public void testIsValidHighScoreCharacterWithValidLatinCharacters() {
		assertTrue("Character 'a' should be valid", gameStatus.isValidHighScoreCharacter('a'));
		assertTrue("Character 'b' should be valid", gameStatus.isValidHighScoreCharacter('a'));
		assertTrue("Character 'x' should be valid", gameStatus.isValidHighScoreCharacter('a'));
		assertTrue("Character 'z' should be valid", gameStatus.isValidHighScoreCharacter('a'));
		assertTrue("Character 'A' should be valid", gameStatus.isValidHighScoreCharacter('A'));
		assertTrue("Character 'B' should be valid", gameStatus.isValidHighScoreCharacter('A'));
		assertTrue("Character 'X' should be valid", gameStatus.isValidHighScoreCharacter('A'));
		assertTrue("Character 'Z' should be valid", gameStatus.isValidHighScoreCharacter('A'));
	}

	@Test
	public void testIsValidHighScoreCharacterWithDigits() {
		assertTrue("Character '0' should be valid", gameStatus.isValidHighScoreCharacter('0'));
		assertTrue("Character '1' should be valid", gameStatus.isValidHighScoreCharacter('1'));
		assertTrue("Character '8' should be valid", gameStatus.isValidHighScoreCharacter('8'));
		assertTrue("Character '9' should be valid", gameStatus.isValidHighScoreCharacter('9'));
	}

	@Test
	public void testIsValidHighScoreCharacterWithSpace() {
		assertTrue("Character ' ' should be valid", gameStatus.isValidHighScoreCharacter(' '));
	}

	@Test
	public void testIsValidHighScoreCharacterWithInvalidSpecialCharacters() {
		assertFalse("Character '!' should not be valid", gameStatus.isValidHighScoreCharacter('!'));
		assertFalse("Character '@' should not be valid", gameStatus.isValidHighScoreCharacter('@'));
		assertFalse("Character '_' should not be valid", gameStatus.isValidHighScoreCharacter('_'));
		assertFalse("Character '-' should not be valid", gameStatus.isValidHighScoreCharacter('-'));
		assertFalse("Character '?' should not be valid", gameStatus.isValidHighScoreCharacter('?'));
		assertFalse("Character '<' should not be valid", gameStatus.isValidHighScoreCharacter('<'));
		assertFalse("Character '>' should not be valid", gameStatus.isValidHighScoreCharacter('>'));
		assertFalse("Character '(' should not be valid", gameStatus.isValidHighScoreCharacter('('));
		assertFalse("Character ')' should not be valid", gameStatus.isValidHighScoreCharacter(')'));
	}

	@Test
	public void testIsValidHighScoreCharacterWithInvalidUtf8Characters() {
		assertFalse("Character '!' should not be valid", gameStatus.isValidHighScoreCharacter('č'));
		assertFalse("Character '@' should not be valid", gameStatus.isValidHighScoreCharacter('ā'));
		assertFalse("Character '_' should not be valid", gameStatus.isValidHighScoreCharacter('ļ'));
		assertFalse("Character '-' should not be valid", gameStatus.isValidHighScoreCharacter('ņ'));
	}
}