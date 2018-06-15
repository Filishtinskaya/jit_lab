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

    void update(byte[] newData) {
        data = newData;

        byte[] nameBytes = this.getName().getBytes();
        byte[] toHash = new byte[nameBytes.length + data.length];
        for (int i=0; i<nameBytes.length; i++)
            toHash[i] = nameBytes[i];
        for (int i=0; i<data.length; i++)
            toHash[i+nameBytes.length] = data[i];

        this.setHash(Helper.byteArrayToHexString(toHash));
    }

    void toObjectFile() throws IOException {
        File objectFile = new File (PathConstants.getObjectsPath() + this.getHash());
        Files.write(objectFile.toPath(), this.getData());
    }

    public byte[] getData() {
        return data;
    }
}
