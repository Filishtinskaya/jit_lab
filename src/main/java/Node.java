import java.io.IOException;
import java.io.Serializable;

abstract class Node implements Serializable {
    private String name, hash;
    private Directory parent;

    abstract void toObjectFile() throws IOException;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }
}
