package lv.herbis.cnakes.tools;

import lv.herbis.cnakes.save.HighScore;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static lv.herbis.cnakes.constants.CnakesConstants.SAVE_FILE_PATH;
import static org.junit.Assert.*;

public class SerializationUtilTest {
	private static final String SERIALIZE_FILE_1 = "test_serialize_file_1.boop";
	private static final String SERIALIZE_FILE_2 = "test_serialize_file_2.boop";
	private static final Path TEST_FILE_PATH_1 = Paths.get(SAVE_FILE_PATH.toString(), SERIALIZE_FILE_1);
	private static final Path TEST_FILE_PATH_2 = Paths.get(SAVE_FILE_PATH.toString(), SERIALIZE_FILE_2);
	private static final HighScore HIGH_SCORE_1 = new HighScore("userName1", 553, 1218545112);
	private static final HighScore HIGH_SCORE_2 = new HighScore("userName2", 533, 1218545112);


	@After
	public void breakDown() throws IOException {
		Files.deleteIfExists(Paths.get(SAVE_FILE_PATH.toString(), SERIALIZE_FILE_1));
		Files.deleteIfExists(Paths.get(SAVE_FILE_PATH.toString(), SERIALIZE_FILE_2));
	}

	@Test
	public void testDeserialize() throws IOException, ClassNotFoundException {
		serializeTestFile(HIGH_SCORE_1, SERIALIZE_FILE_1);
		serializeTestFile(HIGH_SCORE_2, SERIALIZE_FILE_2);

		final Object highScoreObj1 = SerializationUtil.deserialize(SAVE_FILE_PATH, SERIALIZE_FILE_1);
		assertTrue("Object should be instance of HighScore", highScoreObj1 instanceof HighScore);
		final HighScore highScore1 = (HighScore) highScoreObj1;
		assertEquals("Deserialize high score should match high score 1", HIGH_SCORE_1, highScore1);

		final Object highScoreObj2 = SerializationUtil.deserialize(SAVE_FILE_PATH, SERIALIZE_FILE_2);
		assertTrue("Object should be instance of HighScore", highScoreObj2 instanceof HighScore);
		final HighScore highScore2 = (HighScore) highScoreObj2;
		assertEquals("Deserialize high score should match high score 1", HIGH_SCORE_2, highScore2);

		assertNotEquals("Different deserialized high score objects should not be equal.", highScoreObj1, highScore2);

	}

	@Test
	public void testSerialize() throws IOException {
		serializeTestFile(HIGH_SCORE_1, SERIALIZE_FILE_1);
		serializeTestFile(HIGH_SCORE_2, SERIALIZE_FILE_2);

		assertTrue("Serialized File should exist.", Files.exists(TEST_FILE_PATH_1));
		assertTrue("Serialized File should be readable.", Files.isReadable(TEST_FILE_PATH_1));
		assertTrue("Serialized File should be writable.", Files.isWritable(TEST_FILE_PATH_1));

		assertTrue("Serialized File should exist.", Files.exists(TEST_FILE_PATH_2));
		assertTrue("Serialized File should be readable.", Files.isReadable(TEST_FILE_PATH_2));
		assertTrue("Serialized File should be writable.", Files.isWritable(TEST_FILE_PATH_2));
	}

	private void serializeTestFile(final Object obj, final String file) throws IOException {
		SerializationUtil.serialize(obj, SAVE_FILE_PATH, file);
		assertTrue("File should exist", Files.exists(TEST_FILE_PATH_1));
	}
}