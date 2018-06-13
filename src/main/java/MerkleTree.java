/*import java.io.IOError;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class MerkleTree implements Serializable {
    private List<LeafNode> elements = new LinkedList<LeafNode>();
    private CommitNode root;

    MerkleTree() {

    }

    void add(Path p) {
        LeafNode cur = new LeafNode(p);
    }

    void remove(Path p) {

    }

    //nodes - not inner class, private member variables, getters and setters
    class Node implements Serializable {
         String name, hash;
         Node parent, leftChild, rightChild;
    }

    class CommitNode extends Node {
        String commitMessage;
    }

    class LeafNode extends Node
    {
         byte[] data;
         LeafNode(Path p) throws IOException {
             if (p.toFile().isDirectory()) {
                 List<String> l;

             }
             else {
                 data = Files.readAllBytes(p);
                 hash = Helper.byteArrayToHexString(data);
             }

         }
    }
}*/
