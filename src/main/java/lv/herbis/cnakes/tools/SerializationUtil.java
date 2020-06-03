package lv.herbis.cnakes.tools;

import java.io.*;

/**
 * This class is a utility class for performing the serialization and
 * deserialization operations provided the required information.
 *
 * @author hiteshgarg
 */
public class SerializationUtil {

	/**
	 * deserialize to Object from given file. We use the general Object so as
	 * that it can work for any Java Class.
	 */
	public static Object deserialize(final String path, final String fileName) throws IOException, ClassNotFoundException {
		final File file = new File(path);
		file.mkdirs();
		final FileInputStream fis = new FileInputStream(path + fileName);
		final BufferedInputStream bis = new BufferedInputStream(fis);
		try (final ObjectInputStream ois = new ObjectInputStream(bis)) {
			return ois.readObject();
		}
	}


	/**
	 * serialize the given object and save it to given file
	 */
	public static void serialize(final Object obj, final String path, final String fileName) throws IOException {
		final File file = new File(path);
		file.mkdirs();
		final FileOutputStream fos = new FileOutputStream(path + fileName);
		final BufferedOutputStream bos = new BufferedOutputStream(fos);
		try (final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(obj);
		}
	}
}