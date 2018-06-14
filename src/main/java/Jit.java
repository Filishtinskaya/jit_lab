import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class Jit {

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
                        add(args[1]);
                    else
                        System.out.println("Specify file for adding.\n");
                    break;
            }
        }
        catch (IllegalArgumentException ex) {
            commandHelp();
        }
    }

    public static void commandHelp() {
        System.out.println("List of the supported commands:\n");
        for (JitCommand command : JitCommand.values()) {
            System.out.println(command + "\n");
        }
    }

    public static void init() {
        System.out.println ("Initialization of jit-directory.\n");
        new File("./.jit/objects").mkdirs();
        new File("./.jit/staging").mkdirs();
    }

    public static void add(String fileToAdd) {
        MerkleTree stagingArea;

        try {
            if (Files.exists(Paths.get(Helper.getSerializationPath())))
                stagingArea = (MerkleTree) Helper.deserialize();
            else
               stagingArea = new MerkleTree();

            stagingArea.add(fileToAdd);
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
