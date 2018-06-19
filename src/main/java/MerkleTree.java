import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Hash tree structure, that represents staging area.
 */
public class MerkleTree implements Serializable {

    private static final long serialVersionUID = 2L;

    private Commit commit;

    //collection of all the nodes in the tree for convenience
    private List<Node> allElements = new LinkedList<Node> ();

    MerkleTree() {
        Directory root = new Directory();
        root.setName(Paths.get("").toAbsolutePath().toString());
        root.setHash(Helper.byteArrayToHexString(root.getName().getBytes()));
        root.setParent(null);
        commit = new Commit(root);
        allElements.add(commit);
        allElements.add(root);
    }

    /**
     * Converts single files to the nodes and adds them to the tree
     * @param toAdd string, that represents file's path
     * @throws IOException
     */
    void add(String toAdd) throws IOException {
        Path path = Paths.get(toAdd).toAbsolutePath();

        if (!Files.exists(path)) {
            System.out.println("There is no such file in the working area.");
            return;
        }

        if (Files.isDirectory(path)) {
            System.out.println("Jit doesn't support adding directories.");
            return;
        }

        final String toAddName = path.toString();

        FileNode f;

        //checking, if the file was already added to the tree
        Optional<Node> opt = allElements.stream().filter(node -> node.getName().equals(toAddName)).findFirst();
        if (opt.isPresent()) {
            f = (FileNode) opt.get();
            f.update(Files.readAllBytes(path));
            return;
        }

        f = new FileNode(path);
        allElements.add(f);
        Node temp = f;
        Path tempPath = path;
        // 1)adding file to directory structure
        while (true) {
            final String parName = tempPath.getParent().toAbsolutePath().toString(); //making final variable for lambda
            Optional<Node> parOpt = allElements.stream().filter(n -> n.getName().equals(parName)).findFirst(); //try to find parent directory of the file we are adding

            //if directory already exists, we bind it with our Node and break
            if (parOpt.isPresent()) {
                Directory parDir = (Directory) parOpt.get();
                temp.setParent(parDir);
                parDir.getChildren().add(temp);
                break;
            }
            //creating the parent directory and repeating the cycle with it
            else {
                Directory d = new Directory();
                d.setName(parName);
                d.getChildren().add(temp);
                temp.setParent(d);
                allElements.add(d);

                tempPath = tempPath.getParent().toAbsolutePath();
                temp = d;
            }
        }

        // 2)updating hashes
        Directory tempDir = f.getParent();
        while (tempDir!=null) {
            tempDir.updateHash();
            tempDir = tempDir.getParent();
        }
    }

    void remove (String toRemove) {
        try {
            String toRemoveAbsolute = Paths.get(toRemove).toAbsolutePath().toString();
            Optional<Node> optToRemove = allElements.stream().filter(n -> n.getName().equals(toRemoveAbsolute)).findFirst();
            if (!optToRemove.isPresent()) {
                System.out.println("This file is not in the staging area.");
                return;
            }
            Node elementToRemove = optToRemove.get();
            allElements.remove(elementToRemove);
            Directory tempDir = elementToRemove.getParent();
            tempDir.getChildren().remove(elementToRemove);
            while (tempDir!=null) {
                if (tempDir.getChildren().size() == 0 && tempDir!=commit.getRoot()) {
                    tempDir.getParent().getChildren().remove(tempDir);
                    allElements.remove(tempDir);
                }
                else
                    tempDir.updateHash();
                tempDir = tempDir.getParent();
            }
        }
        catch (NoSuchElementException ex) {
            System.out.println("ELement to remove was not found in a staging area.");
            return;
        }
    }

    List<Node> getAllElements() {
        return allElements;
    }

    Commit getCommit() {
        return commit;
    }
}