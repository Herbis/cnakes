package lv.herbis.cnakes.tools;

import lv.herbis.cnakes.entities.Image;
import lv.herbis.cnakes.save.HighScores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static lv.herbis.cnakes.constants.CnakesConstants.HIGH_SCORE_FILE;
import static lv.herbis.cnakes.constants.CnakesConstants.SAVE_FILE_PATH;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * Utility class to help manipulate data.
 */
public class DataUtil {
	private static final Logger LOG = LogManager.getLogger(DataUtil.class);

	private DataUtil() {
		// Only static access.
	}

	public static void removeItemsAboveIndex(final List<?> list, final int maxIndex) {
		if (list == null || maxIndex < 0) {
			if (list == null) {
				LOG.warn("Can't remove items above index from list, because list is null.");
			} else {
				LOG.warn("Can't remove items above index from list, because max index ({}) is below 0.", maxIndex);
			}
			return;
		}

		final int listLastItemIndex = list.size() - 1;
		if (listLastItemIndex > maxIndex) {
			list.subList(maxIndex + 1, listLastItemIndex + 1).clear();
		}
	}

	public static ByteBuffer ioResourceToByteBuffer(final String resource, final int bufferSize) throws IOException {
		ByteBuffer buffer;

		final Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (final SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1) {
					;
				}
			}
		} else {
			try (final InputStream source = DataUtil.class.getClassLoader()
					.getResourceAsStream(resource); final ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = createByteBuffer(bufferSize);

				while (true) {
					final int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(final ByteBuffer buffer, final int newCapacity) {
		final ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	/**
	 * Loads High Scores from a file to the local class.
	 */
	public static HighScores loadHighScores(final int limit) {
		HighScores highScores;

		try {
			highScores = (HighScores) SerializationUtil.deserialize(SAVE_FILE_PATH, HIGH_SCORE_FILE);
			highScores.changeLimit(limit);
		} catch (final Exception e) {
			highScores = new HighScores(limit);
		}

		return highScores;
	}


	/**
	 * Get the accurate system time
	 *
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return System.nanoTime() / 1000000;
	}

	public static Image loadImage(final String path) {
		final ByteBuffer image;
		final int width;
		final int height;
		try (final MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer comp = stack.mallocInt(1);
			final IntBuffer w = stack.mallocInt(1);
			final IntBuffer h = stack.mallocInt(1);

			image = stbi_load(path, w, h, comp, 4);
			if (image == null) {
				throw new IllegalArgumentException("Could not load image resources.");
			}
			width = w.get();
			height = h.get();
		}
		return new Image(width, height, image);
	}
}
