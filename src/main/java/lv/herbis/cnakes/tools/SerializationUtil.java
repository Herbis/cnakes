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
        File file = new File(path);
        file.mkdirs();
        FileInputStream fis = new FileInputStream(path + fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }


    /**
     * serialize the given object and save it to given file
     */
    public static void serialize(Object obj, final String path, final String fileName) throws IOException {
        File file = new File(path);
        file.mkdirs();
        FileOutputStream fos = new FileOutputStream(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }
}