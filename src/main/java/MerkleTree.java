import java.io.Serializable;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class MerkleTree implements Serializable {
    private List<LeafNode> elements = new LinkedList<LeafNode>();
    private Node root;

    MerkleTree() {

    }

    void add(Path p) {

    }

    class Node implements Serializable {
         String hash;
         Node parent, leftChild, rightChild;
    }

    class LeafNode extends Node {
         byte[] data;
    }
}
