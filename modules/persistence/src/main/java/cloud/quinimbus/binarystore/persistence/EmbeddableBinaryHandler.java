package cloud.quinimbus.binarystore.persistence;

import cloud.quinimbus.binarystore.api.Binary;
import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.common.tools.SingletonContextLoader;
import cloud.quinimbus.config.api.ConfigContext;
import cloud.quinimbus.persistence.api.PersistenceContext;
import cloud.quinimbus.persistence.api.entity.EmbeddedObject;
import cloud.quinimbus.persistence.api.entity.EmbeddedPropertyHandler;
import cloud.quinimbus.persistence.api.entity.UnparseableValueException;
import cloud.quinimbus.persistence.api.lifecycle.EntityPostLoadEvent;
import cloud.quinimbus.persistence.api.lifecycle.EntityPreSaveEvent;
import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;

public class EmbeddableBinaryHandler extends EmbeddedPropertyHandler {

    private BinaryStorage storage;

    private final BinaryStoreContext binaryStoreContext;
    private final String storeId;

    public EmbeddableBinaryHandler(PersistenceContext context, String schemaId, String typeId, String property) {
        super(context, schemaId, typeId, property);
        var configContext = ServiceLoader.load(ConfigContext.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find any ConfigContext implementation"));
        var persistenceIntegrationConfig = configContext
                .asNode("binary", "persistence-integration")
                .orElseThrow(() -> new IllegalStateException("binary.persistence-integration is not configured"));
        var schemaConfig = persistenceIntegrationConfig
                .asNode("schemas", schemaId)
                .orElseThrow(() -> new IllegalStateException(
                        "binary.persistence-integration is not configured for schema %s".formatted(schemaId)));
        this.storeId = schemaConfig
                .asString("store")
                .orElseThrow(() -> new IllegalStateException(
                        "store is not configured for the binary.persistence-integration for schema %s"
                                .formatted(schemaId)));
        this.binaryStoreContext = SingletonContextLoader.loadContext(BinaryStoreContext.class, ServiceLoader::load);
    }

    private BinaryStorage getStorage() {
        if (this.storage == null) {
            try {
                this.storage = this.binaryStoreContext
                        .getStorage(this.storeId)
                        .orElseThrow(() -> new IllegalStateException(
                                "Cannot find the configured binary storage %s".formatted(storeId)))
                        .subStorage(this.getTypeId());
            } catch (BinaryStoreException ex) {
                throw new IllegalStateException(
                        "Failed to access the binary substorage %s.%s".formatted(storeId, this.getTypeId()), ex);
            }
        }
        return this.storage;
    }

    public void onPreSave(EntityPreSaveEvent event) {
        System.out.println("[onPreSave]");
        if (event.mutatedProperties().contains(this.getProperty())) {
            System.out.println("[onPreSave] contains " + this.getProperty());
            var entity = event.entity();
            var property = (EmbeddedObject) entity.getProperty(this.getProperty());
            System.out.println("[onPreSave] property " + property);
            String binaryId = property.getProperty("id");
            String binaryContentType = property.getProperty("contentType");
            var binaryNewContent = property.getTransientFields().get("newContent");
            if (binaryId == null && binaryNewContent != null) {
                if (binaryNewContent instanceof EmbeddableBinary.ContentLoader newContentLoader)
                    try (var is = newContentLoader.get()) {
                        var savedBinary = this.getStorage().save(is, binaryContentType);
                        var binary = this.toEmbeddedBinary(savedBinary);
                        entity.setProperty(this.getProperty(), binary);
                        event.changedEntity().accept(entity);
                    } catch (IOException | BinaryStoreException ex) {
                        throw new IllegalStateException("Failed to save a entity binary", ex);
                    }
            }
        }
    }

    public void onPostLoad(EntityPostLoadEvent event) {
        var entity = event.entity();
        var property = (EmbeddedObject) entity.getProperty(this.getProperty());
        if (property != null) {
            var binary = this.toEmbeddedBinaryWithLoader(property);
            entity.setProperty(this.getProperty(), binary);
            event.changedEntity().accept(entity);
        }
    }

    @Override
    public void init() {
        this.onLifecycleEvent(EntityPreSaveEvent.class, this::onPreSave);
        this.onLifecycleEvent(EntityPostLoadEvent.class, this::onPostLoad);
    }

    private EmbeddedObject toEmbeddedBinary(Binary binary) {
        try {
            EmbeddableBinary.BinaryStoreContentLoader contentLoader =
                    () -> this.getStorage().read(binary);
            var properties = Map.<String, Object>of(
                    "id",
                    binary.id(),
                    "contentType",
                    binary.contentType(),
                    "size",
                    binary.size(),
                    "created",
                    binary.created(),
                    "modified",
                    binary.modified());
            var transientFields = Map.<String, Object>of("contentLoader", contentLoader);
            return this.newEmbedded(properties, transientFields);
        } catch (UnparseableValueException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private EmbeddedObject toEmbeddedBinaryWithLoader(EmbeddedObject object) {
        try {
            var properties = object.getProperties();
            EmbeddableBinary.BinaryStoreContentLoader contentLoader =
                    () -> this.getStorage().read((String) properties.get("id"));
            var transientFields = Map.<String, Object>of("contentLoader", contentLoader);
            return this.newEmbedded(properties, transientFields);
        } catch (UnparseableValueException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
