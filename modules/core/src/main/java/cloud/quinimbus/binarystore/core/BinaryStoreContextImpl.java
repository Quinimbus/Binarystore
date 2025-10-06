package cloud.quinimbus.binarystore.core;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import cloud.quinimbus.common.tools.ProviderLoader;
import cloud.quinimbus.tools.function.LazySingletonSupplier;
import cloud.quinimbus.tools.lang.TypeRef;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class BinaryStoreContextImpl implements BinaryStoreContext {

    private final Map<String, BinaryStorage> storages;

    private final Map<String, LazySingletonSupplier<BinaryStorageProvider<? extends BinaryStorage>>> storageProviders;

    public BinaryStoreContextImpl() {
        this.storages = new LinkedHashMap<>();
        this.storageProviders = ProviderLoader.loadProviders(
                new TypeRef<BinaryStorageProvider<? extends BinaryStorage>>() {}, ServiceLoader::load, true);
    }

    @Override
    public Optional<BinaryStorage> getStorage(String id) {
        return Optional.ofNullable(this.storages.get(id));
    }

    @Override
    public void setStorage(String id, BinaryStorage storage) {
        this.storages.put(id, storage);
    }

    @Override
    public <T extends BinaryStorage> Optional<? extends BinaryStorageProvider<T>> getProvider(String alias) {
        return Optional.ofNullable(this.storageProviders.get(alias)).map(p -> (BinaryStorageProvider<T>) p.get());
    }
}
