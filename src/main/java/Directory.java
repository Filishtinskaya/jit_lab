import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

class Directory extends Node {
    private List<Node> children = new LinkedList<Node>();

    /**
     * Function, that is used to update the value of hash, based on value of children's hashes.
     */
    void updateHash() {
        String toHash = this.getName() + children.stream().map(Node::getHash).reduce("", (s1, s2) -> s1+s2);
        this.setHash(Helper.byteArrayToHexString(toHash.getBytes()));
    }

    @Override
    void toObjectFile() throws IOException {
        File objectFile = new File (PathConstants.getObjectsPath() + this.getHash());

        String fileContent = "Directory\n";
        for (Node child : this.getChildren()) {
            if (child.getClass() == Directory.class)
                fileContent += "Directory  ";
            else
                fileContent += "File  ";

            fileContent += child.getHash() + "  " + child.getName() + "\n";
        }
        Files.write(objectFile.toPath(), fileContent.getBytes());
    }

    List<Node> getChildren() {
        return children;
    }
}
