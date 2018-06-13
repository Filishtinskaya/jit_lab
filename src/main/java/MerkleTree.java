import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MerkleTree implements Serializable {

    private Directory curRoot;
    //Collection of all the nodes in the tree for convenience (maybe, not a set?)
    private List<FilesystemElement> allElements = new LinkedList<FilesystemElement> ();

    MerkleTree() {
        Directory root = new Directory();
        root.name = Paths.get(".").toString();
        root.hash = Helper.byteArrayToHexString(root.name.getBytes());
        root.parent = null;
        allElements.add(root);
    }

    abstract class Node implements Serializable {
        protected String name, hash;
    }

    class Commit extends Node {
        private String commitMessage;
        private Directory root;
    }

    abstract class FilesystemElement extends Node {
        protected Directory parent;
    }

    class Directory extends FilesystemElement {
        private List<FilesystemElement> children;

        void updateHash() {
            hash = Helper.byteArrayToHexString(children.stream().map(c -> c.hash).reduce("", (s1, s2) -> s1+s2).getBytes());
        }
    }

    class FileNode extends FilesystemElement {
        private byte[] data;

        FileNode (Path path) throws IOException {
            name = path.toString();
            data = Files.readAllBytes(path);
            hash = Helper.byteArrayToHexString(data);
            //parent = path.;
        }
    }

    //has only functionality of adding single files, not directories
    public void add(String toAdd) {
        try {
            Path path = Paths.get(toAdd);
            FilesystemElement f = new FileNode(path);
            String parName = path.getParent().toString();

            FilesystemElement temp = f;

            //adding file to directory structure
            allElements.add(f);
            do {
                final String finalParName = parName;//for lambda
                Optional<FilesystemElement> parOpt = allElements.stream().filter(n -> n.name.equals(finalParName)).findFirst(); //try to find parent directory of the file we are adding
                if (parOpt.isPresent()) {
                    Directory parDir = (Directory) parOpt.get();
                    temp.parent = parDir;
                    parDir.children.add(temp);
                    break;
                } else {
                    Directory d = new Directory();
                    d.name = parName;
                    d.children.add(f);
                    temp.parent = d;
                    allElements.add(d);

                    path = path.getParent();
                    parName = path.getParent().toString();;
                    temp = d;
                }
            }
            while (true);

            //updating hashes
            Directory tempDir = (Directory) f.parent;
            do {
                tempDir.updateHash();
                tempDir = tempDir.parent;
            }
            while (f!=curRoot);

        }
        catch (IOException ex) {
            System.out.println ("Error while reading the file.\n");
        }
    }
}