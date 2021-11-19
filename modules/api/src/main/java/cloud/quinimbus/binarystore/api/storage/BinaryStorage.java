package cloud.quinimbus.binarystore.api.storage;

import cloud.quinimbus.binarystore.api.Binary;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import java.io.InputStream;
import java.util.Optional;

public interface BinaryStorage {

    BinaryStorage subStorage(String... ident) throws BinaryStoreException;

    Optional<Binary> load(String id) throws BinaryStoreException;

    InputStream read(String id) throws BinaryStoreException;

    default InputStream read(Binary binary) throws BinaryStoreException {
        return this.read(binary.id());
    }

    Binary save(InputStream is, String contentType) throws BinaryStoreException;

    Binary save(String id, InputStream is, String contentType) throws BinaryStoreException;

    default Binary save(Binary binary, InputStream is, String contentType) throws BinaryStoreException {
        return this.save(binary.id(), is, contentType);
    }

    void remove(String id) throws BinaryStoreException;

    default void remove(Binary binary) throws BinaryStoreException {
        this.remove(binary.id());
    }
}
