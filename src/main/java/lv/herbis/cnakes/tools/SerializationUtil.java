package lv.herbis.cnakes.tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class is a utility class for performing the serialization and
 * deserialization operations provided the required information.
 *
 * @author hiteshgarg
 */
public class SerializationUtil {

	private SerializationUtil() {
		// Only static access
	}

	/**
	 * deserialize to Object from given file. We use the general Object so as
	 * that it can work for any Java Class.
	 */
	public static Object deserialize(final Path path,
									 final String fileName) throws IOException, ClassNotFoundException {
		Files.createDirectories(path);
		final BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Path.of(path.toString(), fileName)));
		try (final ObjectInputStream ois = new ObjectInputStream(bis)) {
			return ois.readObject();
		}
	}


	/**
	 * serialize the given object and save it to given file
	 */
	public static void serialize(final Object obj, final Path path, final String fileName) throws IOException {
		Files.createDirectories(path);
		final BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Path.of(path.toString(), fileName)));
		try (final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(obj);
		}
	}
}