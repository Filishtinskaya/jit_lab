import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class Jit {

    enum ModificationType {ADDING, REMOVING};

    public static void main (String[] args) {
        try {
            if (args.length == 0) {
                commandHelp();
                return;
            }
            JitCommand command = JitCommand.valueOf(args[0].toUpperCase());
            switch (command) {
                case INIT: init(); break;
                case ADD:
                    if (args.length >= 2)
                       modify(args[1], ModificationType.ADDING);
                    else
                        System.out.println("Specify file for adding.\n");
                    break;
                case REMOVE:
                    if (args.length >= 2)
                        modify(args[1], ModificationType.REMOVING);
                    else
                        System.out.println("Specify file for removing.\n");
                    break;
            }
        }
        catch (IllegalArgumentException ex) {
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
        System.out.println ("Initialization of jit-directory.\n");
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

            switch(mode) {
                case ADDING: System.out.println ("Adding file to staging area.\n"); stagingArea.add(fileToModify); break;
                case REMOVING: System.out.println ("Removing file of the staging area.\n"); stagingArea.remove(fileToModify);  break;
            }

            Helper.serialize(stagingArea);
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

    /*

    public static void remove(String fileToAdd) throws IOException, ClassNotFoundException {
        MerkleTree stagingArea;
        stagingArea = (MerkleTree) Helper.deserialize();

        Path path = Paths.get(fileToAdd);
        stagingArea.remove(path);
        Helper.serialize(stagingArea);
        //check, that this shit rewrites .ser file, not appends
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
    //just play with console java to make sure, that all the file stuff works as necessary*/
}
