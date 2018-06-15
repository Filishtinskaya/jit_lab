import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
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

    static void clearWorkspace() throws IOException{
        Path pathToBeDeleted = Paths.get(".");
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .filter(path -> !path.toString().contains(".jit"))
                .map(Path::toFile)
                .forEach(File::delete);

        Files.list(Paths.get(".")).filter(p -> !p.toString().endsWith(".jit")).forEach(p -> {
            try{
                Files.delete(p);
            }
            catch (IOException ex){
                System.out.println ("Error while cleaning workspace\n");
                ex.printStackTrace();
            }
        });
    }

    static String getSerializationPath() {
        return SERIALIZATION_PATH;
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
