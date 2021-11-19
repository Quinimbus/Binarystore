package cloud.quinimbus.binarystore.api;

public class BinaryStoreException extends Exception {

    public BinaryStoreException(String message) {
        super(message);
    }

    public BinaryStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
