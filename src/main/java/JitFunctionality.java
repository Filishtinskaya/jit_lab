import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JitFunctionality {

    /**
     * Creates a ".jit" subdirectory in current working directory, that has folders for the working area and commits.
     */
    static void init() {
        System.out.println("Initialization of jit-directory.");
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

        if (Files.exists(Paths.get(PathConstants.getSerializationPath())))
            stagingArea = (MerkleTree) SerializationHelper.deserialize();
        else
            stagingArea = new MerkleTree();

        switch (mode) {
            case ADDING:
                System.out.println("Adding file to staging area.");
                stagingArea.add(fileToModify);
                break;
            case REMOVING:
                 System.out.println("Removing file of the staging area.");
                 stagingArea.remove(fileToModify);
                 break;
           }

           SerializationHelper.serialize(stagingArea);
    }

    static void commit(String message) throws IOException, ClassNotFoundException {
        MerkleTree stagingArea;
        System.out.println("Commiting.");
        if (Files.exists(Paths.get(PathConstants.getSerializationPath())))
            stagingArea = (MerkleTree) SerializationHelper.deserialize();
        else {
            System.out.println("Nothing in the staging area to commit.");
            return;
        }

        List<Node> toCommit = stagingArea.getAllElements();

        //creating object file for commit
        Node root = toCommit.get(0);
        String commitHash = Helper.byteArrayToHexString(root.getHash().getBytes());
        System.out.println ("Commit name: " + commitHash);
        File commitFile = new File(PathConstants.getObjectsPath() + commitHash);
        // !! don't get, why this works for protected access rights?
        String commitContent = message + "\n" + "Directory  " + root.getHash() + "  " + root.getName() + "\n";
        Files.write(commitFile.toPath(), commitContent.getBytes());

        //creating object files for directories and files
        for (Node el : toCommit) {
            Helper.toObjectFile(el);
        }

        Files.delete(Paths.get(PathConstants.getSerializationPath()));
    }

    static void checkout (String commit) throws IOException {
        Path commitObj = Paths.get(PathConstants.getObjectsPath() + commit);

        List<String> commitContent = Files.readAllLines(commitObj);

        Helper.clearWorkspace();

        System.out.println ("Checking out commit \"" + commitContent.get(0) + '"' + "\n");
        String[] rootInfo = commitContent.get(1).split("  ");
        //rootInfo[1] //hash of the root directory - do we need it at all
        //rootInfo[2] //name of the root directory

        Helper.restoreDirectory(rootInfo[1]);
    }
}
