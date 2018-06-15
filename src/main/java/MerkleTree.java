import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MerkleTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private Directory curRoot;

    //Collection of all the nodes in the tree for convenience (maybe, not a set?)
    private List<Node> allElements = new LinkedList<Node> ();

    MerkleTree() {
        Directory root = new Directory();
        root.setName(Paths.get("").toAbsolutePath().toString());
        root.setHash(Helper.byteArrayToHexString(root.getName().getBytes()));
        root.setParent(null);
        curRoot = root;
        allElements.add(root);
    }

    //has only functionality of adding single files, not directories
    public void add(String toAdd) {
        try {
            //System.out.println(curRoot.name + "\n");
            Path path = Paths.get(toAdd).toAbsolutePath();
            final String toAddName = path.toString();

            FileNode f;

            //checking, if the file was already added to the tree
            Optional<Node> opt = allElements.stream().filter(node -> node.getName().equals(toAddName)).findFirst();
            if (opt.isPresent()) {
                f = (FileNode) opt.get();
                f.update(Files.readAllBytes(path));
            }
            else {
                f = new FileNode(path);

                Node temp = f;

                // adding file to directory structure
                allElements.add(f);
                do {
                    final String parName = path.getParent().toAbsolutePath().toString(); //for lambda
                    //System.out.println(allElements.get(0).name + " " + parName + "\n");
                    Optional<Node> parOpt = allElements.stream().filter(n -> n.getName().equals(parName)).findFirst(); //try to find parent directory of the file we are adding
                    if (parOpt.isPresent()) {
                        Directory parDir = (Directory) parOpt.get();
                        temp.setParent(parDir);
                        parDir.getChildren().add(temp);
                        break;
                    } else {
                        Directory d = new Directory();
                        d.setName(parName);
                        d.getChildren().add(temp);
                        temp.setParent(d);
                        allElements.add(d);

                        path = path.getParent().toAbsolutePath();
                        temp = d;
                    }
                }
                while (true);
            }

            // 2)updating hashes
            Directory tempDir = f.getParent();
            do {
                tempDir.updateHash();
                tempDir = tempDir.getParent();
            }
            while (tempDir!=null);

        }
        catch (IOException ex) {
            System.out.println ("Error while reading the file.\n");
        }
    }

    public void remove (String toRemove) {
        try {
            String toRemoveAbsolute = Paths.get(toRemove).toAbsolutePath().toString();
            Node elementToRemove = allElements.stream().filter(n -> n.getName().equals(toRemoveAbsolute)).findFirst().get();
            allElements.remove(elementToRemove);
            Directory tempDir = elementToRemove.getParent();
            do {
                tempDir.getChildren().remove(elementToRemove);
                if (tempDir.getChildren().size() == 0 && tempDir!=curRoot)
                    allElements.remove(tempDir);
                else
                    tempDir.updateHash();
                tempDir = tempDir.getParent();
            }
            while (tempDir!=null);
        }
        catch (NoSuchElementException ex) {
            System.out.println("ELement to remove was not found in a staging area.");
            return;
        }
    }

    public List<Node> getAllElements() {
        return allElements;
    }
}