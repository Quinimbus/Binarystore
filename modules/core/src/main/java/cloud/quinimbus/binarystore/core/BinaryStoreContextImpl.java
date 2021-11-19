package cloud.quinimbus.binarystore.core;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BinaryStoreContextImpl implements BinaryStoreContext {

    private final Map<String, BinaryStorage> storages;

    public BinaryStoreContextImpl() {
        this.storages = new LinkedHashMap<>();
    }

    @Override
    public Optional<BinaryStorage> getStorage(String id) {
        return Optional.ofNullable(this.storages.get(id));
    }

    @Override
    public void setSchemaStorage(String id, BinaryStorage storage) {
        this.storages.put(id, storage);
    }
}
