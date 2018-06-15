import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
                        System.out.println("You have to specify file for adding.");
                    break;
                case REMOVE:
                    if (args.length >= 2)
                        modify(args[1], ModificationType.REMOVING);
                    else
                        System.out.println("You have to specify file for removing.");
                    break;
                case COMMIT:
                    if (args.length >= 2)
                        commit (args[1]);
                    else
                        System.out.println("You have to write commit message.");
                    break;
                case CHECKOUT:
                    if (args.length >= 2)
                        checkout(args[1]);
                    else
                        System.out.println("You have to give the hash of the commit.");
            }
        } catch (IllegalArgumentException ex) {
            commandHelp();
        }
    }

    private static void commandHelp() {
        System.out.println("List of the supported commands:");
        for (JitCommand command : JitCommand.values()) {
            System.out.println(command + "\n");
        }
    }

    private static void init() {
        System.out.println("Initialization of jit-directory.");
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
                    System.out.println("Adding file to staging area.");
                    stagingArea.add(fileToModify);
                    break;
                case REMOVING:
                    System.out.println("Removing file of the staging area.");
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
            String commitContent = message + "\n" + "Directory  " + root.hash + "  " + root.name + "\n";
            Files.write(commitFile.toPath(), commitContent.getBytes());

            for (MerkleTree.Node el : toCommit) {
                File objectFile = new File (OBJECTS_PATH + el.hash);
                //Files.createFile(objectFile.toPath());
                if (el.getClass() == MerkleTree.Directory.class) {
                    String fileContent = "Directory\n";
                    MerkleTree.Directory dir = (MerkleTree.Directory) el;
                    for (MerkleTree.Node child : dir.getChildren()) {
                        if (child.getClass() == MerkleTree.Directory.class)
                            fileContent += "Directory  ";
                        else
                            fileContent += "File  ";

                        fileContent += child.hash + "  " + child.name + "\n";
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

    private static void checkout (String commit) {
        try {
            Path commitObj = Paths.get(OBJECTS_PATH + commit);


            List<String> commitContent = Files.readAllLines(commitObj);

            Helper.clearWorkspace();

            System.out.println ("Checking out commit \"" + commitContent.get(0) + '"' + "\n");
            String[] rootInfo = commitContent.get(1).split("  ");
            //rootInfo[1] //hash of the root directory - do we need it at all
            //rootInfo[2] //name of the root directory
            //Files.createDirectory(Paths.get(rootInfo[2]));

            restoreDirectory(rootInfo[1]);
        } catch (IOException ex) {
            System.out.println("Something went wrong with object files IO.");
            ex.printStackTrace();
        }
    }

    static void restoreDirectory (String hash) {
        try {
            List<String> content = Files.readAllLines(Paths.get(OBJECTS_PATH + hash));
            content.remove(0);
            for (String line : content) {
                System.out.println(line);
                String[] childInfo = line.split("  ");
                if (childInfo[0].equals("Directory")) {
                    Files.createDirectory(Paths.get(childInfo[2]));
                    restoreDirectory(childInfo[1]);
                }
                else {
                    Files.copy(Paths.get(OBJECTS_PATH + childInfo[1]), Paths.get(childInfo[2]));
                }
            }
        }
        catch (IOException ex) {
            System.out.println("Something went wrong with object files IO.");
            ex.printStackTrace();
        }

    }
}
