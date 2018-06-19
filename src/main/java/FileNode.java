import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileNode extends Node {
    private byte[] data;

    FileNode(Path path) throws IOException {
        this.setName(path.toAbsolutePath().toString());
        update(Files.readAllBytes(path));
    }

    /**
     * Changes the content of the node and re-counts hash(based on the name and the content).
     * @param newData new value of node's content
     */
    void update(byte[] newData) {
        data = newData;

        byte[] nameBytes = this.getName().getBytes();

        //Copying two byte arrays into one.
        byte[] toHash = new byte[nameBytes.length + data.length];
        for (int i=0; i<nameBytes.length; i++)
            toHash[i] = nameBytes[i];
        for (int i=0; i<data.length; i++)
            toHash[i+nameBytes.length] = data[i];

        this.setHash(Helper.byteArrayToHexString(toHash));
    }

    @Override
    void toObjectFile() throws IOException {
        File objectFile = new File (PathConstants.getObjectsPath() + this.getHash());
        Files.write(objectFile.toPath(), this.getData());
    }

    byte[] getData() {
        return data;
    }
}
