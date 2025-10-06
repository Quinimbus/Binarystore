package cloud.quinimbus.binarystore.api;

import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import java.util.Optional;

public interface BinaryStoreContext {

    Optional<BinaryStorage> getStorage(String id);

    void setStorage(String id, BinaryStorage storage);

    <T extends BinaryStorage> Optional<? extends BinaryStorageProvider<T>> getProvider(String alias);

    ContentTypeDetector getContentTypeDetector();
}
