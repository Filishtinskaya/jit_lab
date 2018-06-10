import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class Jit {

    public static void init() throws IOException
    {
        new File("./.jit/objects").mkdirs();
        new File("./.jit/staging").mkdirs();
        MerkleTree stagingArea = new MerkleTree();
        Helper.serialize(stagingArea);
    }

    public static void add(String fileToAdd) throws IOException, ClassNotFoundException {
        MerkleTree stagingArea;
        stagingArea = (MerkleTree) Helper.deserialize();

        Path path = Paths.get(fileToAdd);
        stagingArea.add(path);
        Helper.serialize(stagingArea);
        //check, that this shit does writing, not appending
    }

    public static void remove() {

    }

    public static void commit() {

    }

    public static void checkout(String commitHash) throws IOException {
        //delete serializable?

        //deleting all the files in the workspace
        File dir = new File(".");
        File[] toDelete = dir.listFiles(f -> f.getName()!=".jit");
        //i just hope that this deletes a directory with all content
        for (File f : toDelete) {
            f.delete();
        }
        Helper.restore(commitHash, ".");
    }

    //handle exceptions (there is an option for closing streams?)
    //backup, so this shit doesn't delete itself
    //just play with console java to make sure, that all the file stuff works as necessary
}
