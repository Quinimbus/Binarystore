package cloud.quinimbus.binarystore.api;

import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import java.util.Optional;

public interface BinaryStoreContext {

    Optional<BinaryStorage> getStorage(String id);

    void setSchemaStorage(String id, BinaryStorage storage);
}
