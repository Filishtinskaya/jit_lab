import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Helper {
    private static final String SERIALIZATION_PATH = "./.jit/staging/staging.ser";

    static void serialize(Object obj) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(SERIALIZATION_PATH)));
        out.writeObject(obj);
        out.close();
    }

    static Object deserialize() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(SERIALIZATION_PATH));
        Object obj = in.readObject();
        in.close();
        return obj;
    }

    static void restore (String hash, String path) throws IOException {
        List<String> content = Files.readAllLines(Paths.get("./jit/objects" + hash));
        //i just hope, that user won't write files, starting with Commit or Directory
        if (content.get(0).startsWith("Commit"))
            content.remove(0);
        if (content.get(0).startsWith("Directory")) {
            content.remove(0);
            for (String line : content) {
                //ok, now it gets a bit tricky
                String[] pars = l.split (" ");
                restore(pars[1], path + '/' + pars[2]);
            }
        }
        else {
            File f = new File(path);
            for (String line : content)
                Files.write(f.toPath(), line.getBytes());
        }
    }



    public static String byteArrayToHexString(byte[] content) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digested = md.digest(content);

            StringBuilder s = new StringBuilder();
            for(byte b : digested) {
                int value = b & 0xFF; // & 0xFF to treat byte as "unsigned"
                s.append(Integer.toHexString(value & 0x0F));
                s.append(Integer.toHexString(value >>> 4));
            }
            return s.toString();
        } catch (NoSuchAlgorithmException ex) {
            // ...
        }
        return null;
    }
}
