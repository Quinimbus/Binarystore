package cloud.quinimbus.binarystore.api.storage;

import cloud.quinimbus.binarystore.api.BinaryStoreException;
import java.util.Map;

public interface BinaryStorageProvider<T extends BinaryStorage> {

    T createStorage(Map<String, Object> params) throws BinaryStoreException;
}
