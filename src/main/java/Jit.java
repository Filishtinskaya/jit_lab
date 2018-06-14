import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Jit {

    // add comments!
    // modify access rights for Node, add getters

    enum ModificationType {ADDING, REMOVING};
    private static final String OBJECTS_PATH = "./.jit/objects/";

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                commandHelp();
                return;
            }
            JitCommand command = JitCommand.valueOf(args[0].toUpperCase());
            switch (command) {
                case INIT:
                    init();
                    break;
                case ADD:
                    if (args.length >= 2)
                        modify(args[1], ModificationType.ADDING);
                    else
                        System.out.println("You have to specify file for adding.\n");
                    break;
                case REMOVE:
                    if (args.length >= 2)
                        modify(args[1], ModificationType.REMOVING);
                    else
                        System.out.println("You have to specify file for removing.\n");
                    break;
                case COMMIT:
                    if (args.length >= 2)
                        commit (args[1]);
                    else
                        System.out.println("You have to write commit message. \n");
            }
        } catch (IllegalArgumentException ex) {
            commandHelp();
        }
    }

    private static void commandHelp() {
        System.out.println("List of the supported commands:\n");
        for (JitCommand command : JitCommand.values()) {
            System.out.println(command + "\n");
        }
    }

    private static void init() {
        System.out.println("Initialization of jit-directory.\n");
        new File("./.jit/objects").mkdirs();
        new File("./.jit/staging").mkdirs();
    }


    private static void modify(String fileToModify, ModificationType mode) {
        MerkleTree stagingArea;

        try {
            if (Files.exists(Paths.get(Helper.getSerializationPath())))
                stagingArea = (MerkleTree) Helper.deserialize();
            else
                stagingArea = new MerkleTree();

            switch (mode) {
                case ADDING:
                    System.out.println("Adding file to staging area.\n");
                    stagingArea.add(fileToModify);
                    break;
                case REMOVING:
                    System.out.println("Removing file of the staging area.\n");
                    stagingArea.remove(fileToModify);
                    break;
            }

            Helper.serialize(stagingArea);
        } catch (IOException ex) {
            System.out.println("Something went wrong with [de]serialization(files IO).");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Something went wrong with [de]serialization(class not found).");
            ex.printStackTrace();
        }
    }

    private static void commit(String message) {
        MerkleTree stagingArea;
        System.out.println("Commiting.\n");
        try {
            if (Files.exists(Paths.get(Helper.getSerializationPath())))
                stagingArea = (MerkleTree) Helper.deserialize();
            else {
                System.out.println("Nothing in the staging area to commit.");
                return;
            }

            List<MerkleTree.Node> toCommit = stagingArea.getAllElements();

            MerkleTree.Node root = toCommit.get(0);
            String commitHash = Helper.byteArrayToHexString(root.hash.getBytes());
            File commitFile = new File(OBJECTS_PATH + commitHash);
            //Files.createFile(commitFile.toPath());
            String commitContent = message + "\n" + "Directory " + root.hash + " " + root.name + "\n";
            Files.write(commitFile.toPath(), commitContent.getBytes());

            for (MerkleTree.Node el : toCommit) {
                File objectFile = new File (OBJECTS_PATH + el.hash);
                //Files.createFile(objectFile.toPath());
                if (el.getClass() == MerkleTree.Directory.class) {
                    String fileContent = "Directory\n";
                    MerkleTree.Directory dir = (MerkleTree.Directory) el;
                    for (MerkleTree.Node child : dir.getChildren()) {
                        if (child.getClass() == MerkleTree.Directory.class)
                            fileContent += "Directory ";
                        else
                            fileContent += "File ";

                        fileContent += child.hash + " " + child.name + "\n";
                    }
                    Files.write(objectFile.toPath(), fileContent.getBytes());
                }
                else {
                    MerkleTree.FileNode fn = (MerkleTree.FileNode) el;
                    Files.write(objectFile.toPath(), fn.getData());
                }
            }

            Files.delete(Paths.get(Helper.getSerializationPath()));
        } catch (IOException ex) {
            System.out.println("Something went wrong with [de]serialization(files IO).");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.out.println("Something went wrong with [de]serialization(class not found).");
            ex.printStackTrace();
        }
    }
}
    /*

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
}*/
