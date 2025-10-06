package cloud.quinimbus.binarystore.core;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import cloud.quinimbus.common.tools.ProviderLoader;
import cloud.quinimbus.common.tools.SingletonContextLoader;
import cloud.quinimbus.config.api.ConfigContext;
import cloud.quinimbus.tools.function.LazySingletonSupplier;
import cloud.quinimbus.tools.lang.TypeRef;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class BinaryStoreContextImpl implements BinaryStoreContext {

    private final Map<String, BinaryStorage> storages;

    private final Map<String, LazySingletonSupplier<BinaryStorageProvider<? extends BinaryStorage>>> storageProviders;

    private final Map<String, LazySingletonSupplier<ContentTypeDetector>> contentTypeDetectors;

    private final Optional<String> contentTypeDetector;

    public BinaryStoreContextImpl() {
        this.storages = new LinkedHashMap<>();
        this.storageProviders = ProviderLoader.loadProviders(
                new TypeRef<BinaryStorageProvider<? extends BinaryStorage>>() {}, ServiceLoader::load, true);
        this.contentTypeDetectors = ProviderLoader.loadProviders(ContentTypeDetector.class, ServiceLoader::load, true);
        var configContext = SingletonContextLoader.loadContext(ConfigContext.class, ServiceLoader::load);
        this.contentTypeDetector = configContext.asString("binary", "content-type", "detector");
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

    @Override
    public ContentTypeDetector getContentTypeDetector() {
        return this.contentTypeDetector
                .map(this.contentTypeDetectors::get)
                .or(() -> this.contentTypeDetectors.values().stream().findFirst())
                .map(LazySingletonSupplier::get)
                .orElseThrow();
    }
}
