public class PathConstants {
    private static final String SERIALIZATION_PATH = "./.jit/staging/staging.ser";
    private static final String OBJECTS_PATH = "./.jit/objects/";

    static String getSerializationPath() {
        return SERIALIZATION_PATH;
    }

    static String getObjectsPath() {
        return OBJECTS_PATH;
    }
}
