import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Commit extends Node {
    private Directory root;

    Commit(Directory r) {
        root = r;
        this.setHash(Helper.byteArrayToHexString(root.getHash().getBytes()));
        this.setParent(null);
        this.setName("");
    }

    @Override
    void toObjectFile() throws IOException {
        System.out.println ("Commit name: " + this.getHash());
        File commitFile = new File(PathConstants.getObjectsPath() + this.getHash());
        String commitContent = this.getName() + "\n" + "Directory  " + root.getHash() + "  " + root.getName() + "\n";
        Files.write(commitFile.toPath(), commitContent.getBytes());
    }

    Directory getRoot() {
        return root;
    }
}
