package cloud.quinimbus.binarystore.api.storage;

import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.config.api.ConfigNode;
import java.util.Map;

public interface BinaryStorageProvider<T extends BinaryStorage> {

    T createStorage(Map<String, Object> params) throws BinaryStoreException;

    T createStorage(ConfigNode config) throws BinaryStoreException;
}
