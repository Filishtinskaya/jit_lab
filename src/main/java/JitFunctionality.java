import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Class, that has internal implementation of Jit commands.
 */
public class JitFunctionality {

    /**
     * Creates a ".jit" subdirectory in current working directory, that has folders for the working area and commits.
     */
    static void init() {
        System.out.println("Initialization of jit-directory...");
        new File(PathConstants.getSerializationPath()).mkdirs();
        new File(PathConstants.getObjectsPath()).mkdirs();
    }

    /**
     * Method, that adds or removes file from staging area.
     * @param fileToModify name of file
     * @param mode shows, if file has to be added or removed
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static void modify(String fileToModify, ModificationType mode) throws IOException, ClassNotFoundException {
        MerkleTree stagingArea;

        //Deserializing MerkleTree, that represents staging area. Serialization is used, because information about staged files has to be stored in the filesystem between method calls.
        File f = new File(PathConstants.getSerializationPath());
        if (f.exists())
            stagingArea = (MerkleTree) SerializationHelper.deserialize();
        else
            stagingArea = new MerkleTree();

        //Modifying MerkleTree accordingly.
        switch (mode) {
            case ADDING:
                System.out.println("Adding file to staging area...");
                stagingArea.add(fileToModify);
                break;
            case REMOVING:
                 System.out.println("Removing file of the staging area...");
                 stagingArea.remove(fileToModify);
                 break;
           }

           //Serializing MerkleTree back.
           SerializationHelper.serialize(stagingArea);
    }

    /**
     * Writes staging area to the objects directory. Names for files are hashes of nodes in the Merkle Tree.
     * Files are written "as is".
     * Directories' object files reflect filesystem structure.
     * Commits' object files have root directory and commit message.
     * @param message description of the commit
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static void commit(String message) throws IOException, ClassNotFoundException {
        MerkleTree stagingArea;
        List<Node> toCommit;
        System.out.println("Commiting...");

        class EmptyStagingArea extends Exception {}

        try {
            if (!Files.exists(Paths.get(PathConstants.getSerializationPath())))
                throw new EmptyStagingArea();
            stagingArea = (MerkleTree) SerializationHelper.deserialize();
            toCommit = stagingArea.getAllElements();
            if (toCommit.size() == 2)
                throw new EmptyStagingArea();
        }
        catch (EmptyStagingArea ex) {
            System.out.println("Nothing in the staging area to commit.");
            return;
        }

        stagingArea.getCommit().setName(message);

        //creating object files for commits, directories and files
        for (Node el : toCommit) {
            el.toObjectFile();
        }

        //clearing staging area
        Files.delete(Paths.get(PathConstants.getSerializationPath()));
    }

    /**
     * Returns directory to the state of the given commit.
     * @param commit
     * @throws IOException
     */
    static void checkout (String commit) throws IOException {
        Path commitObj = Paths.get(PathConstants.getObjectsPath() + commit);
        if (!commitObj.toFile().exists()) {
            System.out.println("There is no commit with that hash.");
            return;
        }

        List<String> commitContent = Files.readAllLines(commitObj);

        Helper.clearWorkspace();

        System.out.println ("Checking out commit \"" + commitContent.get(0) + '"' + "\n");
        String[] rootInfo = commitContent.get(1).split("  ");

        //rootInfo[1] is hash of the root directory
        Helper.restoreDirectory(rootInfo[1]);
    }
}
