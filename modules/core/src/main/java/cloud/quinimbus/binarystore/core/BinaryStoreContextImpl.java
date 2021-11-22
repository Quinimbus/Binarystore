package cloud.quinimbus.binarystore.core;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import cloud.quinimbus.common.annotations.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class BinaryStoreContextImpl implements BinaryStoreContext {

    private final Map<String, BinaryStorage> storages;

    private final Map<String, BinaryStorageProvider<? extends BinaryStorage>> storageProviders;

    public BinaryStoreContextImpl() {
        this.storages = new LinkedHashMap<>();
        this.storageProviders = new LinkedHashMap<>();
        ServiceLoader.load(BinaryStorageProvider.class).forEach(bsp -> {
            var providerAnno = bsp.getClass().getAnnotation(Provider.class);
            if (providerAnno == null) {
                throw new IllegalStateException(
                        "Binary storage provider %s is missing the @Provider annotation"
                                .formatted(bsp.getClass().getName()));
            }
            for (String a : providerAnno.alias()) {
                this.storageProviders.put(a, bsp);
            }
        });
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
        return Optional.ofNullable((BinaryStorageProvider<T>) this.storageProviders.get(alias));
    }
}
