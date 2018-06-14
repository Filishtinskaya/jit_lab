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
        root.name = Paths.get("").toAbsolutePath().toString();
        root.hash = Helper.byteArrayToHexString(root.name.getBytes());
        root.parent = null;
        curRoot = root;
        allElements.add(root);
    }

    abstract class Node implements Serializable {
        protected String name, hash;
        protected Directory parent;
    }

    class Directory extends Node {
        private List<Node> children = new LinkedList<Node>();

        void updateHash() {
            String toHash = name + children.stream().map(c->c.hash).reduce("", (s1, s2) -> s1+s2);
            hash = Helper.byteArrayToHexString(toHash.getBytes());
        }

        public List<Node> getChildren() {
            return children;
        }
    }

    class FileNode extends Node {
        private byte[] data;

        FileNode (Path path) throws IOException {
            name = path.toAbsolutePath().toString();
            data = Files.readAllBytes(path);

            byte[] nameBytes = name.getBytes();
            byte[] toHash = new byte[nameBytes.length + data.length];
            for (int i=0; i<nameBytes.length; i++)
                toHash[i] = nameBytes[i];
            for (int i=0; i<data.length; i++)
                toHash[i+nameBytes.length] = data[i];

            hash = Helper.byteArrayToHexString(toHash);
        }

        public byte[] getData() {
            return data;
        }
    }

    //has only functionality of adding single files, not directories
    public void add(String toAdd) {
        try {
            //System.out.println(curRoot.name + "\n");
            Path path = Paths.get(toAdd).toAbsolutePath();
            Node f = new FileNode(path);

            Node temp = f;

            // 1)adding file to directory structure
            allElements.add(f);
            do {
                final String parName = path.getParent().toAbsolutePath().toString(); //for lambda
                //System.out.println(allElements.get(0).name + " " + parName + "\n");
                Optional<Node> parOpt = allElements.stream().filter(n -> n.name.equals(parName)).findFirst(); //try to find parent directory of the file we are adding
                if (parOpt.isPresent()) {
                    Directory parDir = (Directory) parOpt.get();
                    temp.parent = parDir;
                    parDir.children.add(temp);
                    break;
                } else {
                    Directory d = new Directory();
                    d.name = parName;
                    d.children.add(temp);
                    temp.parent = d;
                    allElements.add(d);

                    path = path.getParent().toAbsolutePath();
                    temp = d;
                }
            }
            while (true);

            // 2)updating hashes
            Directory tempDir = f.parent;
            do {
                tempDir.updateHash();
                tempDir = tempDir.parent;
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
            Node elementToRemove = allElements.stream().filter(n -> n.name.equals(toRemoveAbsolute)).findFirst().get();
            allElements.remove(elementToRemove);
            Directory tempDir = elementToRemove.parent;
            do {
                tempDir.children.remove(elementToRemove);
                if (tempDir.children.size() == 0 && tempDir!=curRoot)
                    allElements.remove(tempDir);
                else
                    tempDir.updateHash();
                tempDir = tempDir.parent;
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