import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;

public class Helper {

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

    static void restoreDirectory (String hash) {
        try {
            List<String> content = Files.readAllLines(Paths.get(PathConstants.getObjectsPath() + hash));
            content.remove(0);
            for (String line : content) {
                String[] childInfo = line.split("  ");
                System.out.println("Restoring " + childInfo[2]);
                if (childInfo[0].equals("Directory")) {
                    Files.createDirectory(Paths.get(childInfo[2]));
                    restoreDirectory(childInfo[1]);
                }
                else {
                    Files.copy(Paths.get(PathConstants.getObjectsPath() + childInfo[1]), Paths.get(childInfo[2]));
                }
            }
        }
        catch (IOException ex) {
            System.out.println("Something went wrong with object files IO.");
            ex.printStackTrace();
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
