import java.io.*;

public class SerializationHelper {
    static void serialize(Object obj) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(PathConstants.getSerializationPath())));
        out.writeObject(obj);
        out.close();
    }

    static Object deserialize() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(PathConstants.getSerializationPath()));
        Object obj = in.readObject();
        in.close();
        return obj;
    }
}
