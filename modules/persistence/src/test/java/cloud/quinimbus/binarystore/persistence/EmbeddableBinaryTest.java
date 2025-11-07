package cloud.quinimbus.binarystore.persistence;

import static org.junit.jupiter.api.Assertions.*;

import cloud.quinimbus.binarystore.api.BinaryStoreContext;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.common.tools.Records;
import cloud.quinimbus.common.tools.SingletonContextLoader;
import cloud.quinimbus.persistence.api.PersistenceContext;
import cloud.quinimbus.persistence.api.PersistenceException;
import cloud.quinimbus.persistence.api.annotation.Entity;
import cloud.quinimbus.persistence.api.annotation.EntityField;
import cloud.quinimbus.persistence.api.annotation.EntityIdField;
import cloud.quinimbus.persistence.api.annotation.Schema;
import cloud.quinimbus.persistence.api.entity.EntityReaderInitialisationException;
import cloud.quinimbus.persistence.api.entity.EntityWriterInitialisationException;
import cloud.quinimbus.persistence.api.lifecycle.EntityPostSaveEvent;
import cloud.quinimbus.persistence.api.schema.InvalidSchemaException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmbeddableBinaryTest {

    private PersistenceContext persistenceContext;

    @Entity(schema = @Schema(id = "unit-test", version = 1))
    public record MyEntity(@EntityIdField String id, EmbeddableBinary binary) {}

    @Entity(schema = @Schema(id = "unit-test", version = 1))
    public record MyListEntity(
            @EntityIdField String id,
            @EntityField(type = EmbeddableBinary.class) List<EmbeddableBinary> binaries) {}

    @Entity(schema = @Schema(id = "unit-test", version = 1))
    public record MyValidatedEntity(
            @EntityIdField String id,

            @ValidateBinary(contentType = {"application/json"})
            EmbeddableBinary binary) {}

    @Entity(schema = @Schema(id = "unit-test", version = 1))
    public record MyValidatedListEntity(
            @EntityIdField String id,

            @EntityField(type = EmbeddableBinary.class) @ValidateBinary(contentType = {"application/json"})
            List<EmbeddableBinary> binary) {}

    @BeforeEach
    public void init() throws IOException, BinaryStoreException {
        this.persistenceContext =
                ServiceLoader.load(PersistenceContext.class).findFirst().get();
        var binaryStoreContext = SingletonContextLoader.loadContext(BinaryStoreContext.class, ServiceLoader::load);
        var tmpPath = Files.createTempDirectory("quinimbus-persistence-test");
        binaryStoreContext.setStorage(
                "persistence-test",
                binaryStoreContext.getProvider("file").get().createStorage(Map.of("rootPath", tmpPath.toString())));
    }

    @Test
    public void testWriteAndReadNullBinary()
            throws InvalidSchemaException, EntityWriterInitialisationException, EntityReaderInitialisationException,
                    PersistenceException {
        var schema = this.persistenceContext.importRecordSchema(MyEntity.class);
        var storage = this.persistenceContext.setInMemorySchemaStorage(schema.id());
        var typeId = Records.idFromRecordClass(MyEntity.class);
        var entityType = schema.entityTypes().get(typeId);
        var writer = this.persistenceContext.getRecordEntityWriter(entityType, MyEntity.class);
        var reader = this.persistenceContext.getRecordEntityReader(entityType, MyEntity.class);

        this.persistenceContext.onLifecycleEvent(schema.id(), EntityPostSaveEvent.class, entityType, e -> {});

        var entity = new MyEntity("1", null);
        storage.save(reader.read(entity));
        var loadedEntity = storage.find(entityType, "1").map(writer::write).orElseThrow();
        assertNull(loadedEntity.binary());
    }

    @Test
    public void testWriteAndReadBinary()
            throws InvalidSchemaException, EntityWriterInitialisationException, EntityReaderInitialisationException,
                    PersistenceException, BinaryStoreException, IOException {
        var schema = this.persistenceContext.importRecordSchema(MyEntity.class);
        var storage = this.persistenceContext.setInMemorySchemaStorage(schema.id());
        var typeId = Records.idFromRecordClass(MyEntity.class);
        var entityType = schema.entityTypes().get(typeId);
        var writer = this.persistenceContext.getRecordEntityWriter(entityType, MyEntity.class);
        var reader = this.persistenceContext.getRecordEntityReader(entityType, MyEntity.class);

        var entity = new MyEntity(
                "1",
                EmbeddableBinary.newBinary(
                        "plain/text",
                        () -> new ByteArrayInputStream("Hello World!".getBytes(Charset.forName("UTF-8")))));
        storage.save(reader.read(entity));
        var loadedEntity = storage.find(entityType, "1").map(writer::write).orElseThrow();
        assertNotNull(loadedEntity.binary().id(), "binary id missing");
        assertNotNull(loadedEntity.binary().contentLoader(), "binary content loader missing");
        try (var is = loadedEntity.binary.contentLoader().get()) {
            assertEquals("Hello World!", new String(is.readAllBytes()));
        }
    }

    @Test
    public void testWriteAndReadBinaryList()
            throws InvalidSchemaException, EntityWriterInitialisationException, EntityReaderInitialisationException,
                    PersistenceException, BinaryStoreException, IOException {
        var schema = this.persistenceContext.importRecordSchema(MyListEntity.class);
        var storage = this.persistenceContext.setInMemorySchemaStorage(schema.id());
        var typeId = Records.idFromRecordClass(MyListEntity.class);
        var entityType = schema.entityTypes().get(typeId);
        var writer = this.persistenceContext.getRecordEntityWriter(entityType, MyListEntity.class);
        var reader = this.persistenceContext.getRecordEntityReader(entityType, MyListEntity.class);

        var entity = new MyListEntity(
                "1",
                List.of(EmbeddableBinary.newBinary(
                        "plain/text",
                        () -> new ByteArrayInputStream("Hello World!".getBytes(Charset.forName("UTF-8"))))));
        storage.save(reader.read(entity));
        var loadedEntity = storage.find(entityType, "1").map(writer::write).orElseThrow();
        assertEquals(1, loadedEntity.binaries().size());
        assertNotNull(loadedEntity.binaries().get(0).id(), "binary id missing");
        assertNotNull(loadedEntity.binaries().get(0).contentLoader(), "binary content loader missing");
        try (var is = loadedEntity.binaries().get(0).contentLoader().get()) {
            assertEquals("Hello World!", new String(is.readAllBytes()));
        }
    }

    @Test
    public void testWriteAndReadValidatedInvalidBinary()
            throws InvalidSchemaException, EntityWriterInitialisationException, EntityReaderInitialisationException,
                    PersistenceException, BinaryStoreException, IOException {
        var schema = this.persistenceContext.importRecordSchema(MyValidatedEntity.class);
        var storage = this.persistenceContext.setInMemorySchemaStorage(schema.id());
        var typeId = Records.idFromRecordClass(MyValidatedEntity.class);
        var entityType = schema.entityTypes().get(typeId);
        var reader = this.persistenceContext.getRecordEntityReader(entityType, MyValidatedEntity.class);

        var entity = new MyValidatedEntity(
                "1",
                EmbeddableBinary.newBinary(
                        "plain/text",
                        () -> new ByteArrayInputStream("Hello World!".getBytes(Charset.forName("UTF-8")))));
        var ex = assertThrows(IllegalArgumentException.class, () -> storage.save(reader.read(entity)));
        assertEquals("Content-Type plain/text not allowed", ex.getMessage());
    }

    @Test
    public void testWriteAndReadValidatedInvalidBinaryList()
            throws InvalidSchemaException, EntityWriterInitialisationException, EntityReaderInitialisationException,
                    PersistenceException, BinaryStoreException, IOException {
        var schema = this.persistenceContext.importRecordSchema(MyValidatedListEntity.class);
        var storage = this.persistenceContext.setInMemorySchemaStorage(schema.id());
        var typeId = Records.idFromRecordClass(MyValidatedListEntity.class);
        var entityType = schema.entityTypes().get(typeId);
        var reader = this.persistenceContext.getRecordEntityReader(entityType, MyValidatedListEntity.class);

        var entity = new MyValidatedListEntity(
                "1",
                List.of(EmbeddableBinary.newBinary(
                        "plain/text",
                        () -> new ByteArrayInputStream("Hello World!".getBytes(Charset.forName("UTF-8"))))));
        var ex = assertThrows(IllegalArgumentException.class, () -> storage.save(reader.read(entity)));
        assertEquals("Content-Type plain/text not allowed", ex.getMessage());
    }
}
