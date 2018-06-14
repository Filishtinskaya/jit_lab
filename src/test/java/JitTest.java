import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class JitTest {
    private static final String SERIALIZATION_PATH = "E:\\ADVANCED JAVA PROGRAMMING\\ToTestJit\\.jit\\staging\\staging.ser";
    @Test
    public void testOfAdding() {
        MerkleTree stagingArea;
        try {
            assertTrue(Files.exists(Paths.get(SERIALIZATION_PATH)));
            stagingArea = (MerkleTree) deserializeForTest();
            System.out.println(stagingArea.toString());
        }
        catch (IOException ex) {
            System.out.println("Something went wrong with [de]serialization(files IO).");
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Something went wrong with [de]serialization(class not found).");
            ex.printStackTrace();
        }
    }

    static Object deserializeForTest() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(SERIALIZATION_PATH));
        Object obj = in.readObject();
        in.close();
        return obj;
    }
}
