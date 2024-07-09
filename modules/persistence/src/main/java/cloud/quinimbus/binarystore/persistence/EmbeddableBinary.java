package cloud.quinimbus.binarystore.persistence;

import cloud.quinimbus.binarystore.api.Binary;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.persistence.api.annotation.Embeddable;
import cloud.quinimbus.persistence.api.annotation.EntityTransientField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.io.InputStream;
import java.time.Instant;
import java.util.function.Supplier;

@RecordBuilder
@Embeddable(handler = EmbeddableBinaryHandler.class)
public record EmbeddableBinary(
        String id,
        String contentType,
        Long size,
        Instant created,
        Instant modified,
        @EntityTransientField @JsonIgnore BinaryStoreContentLoader contentLoader,
        @EntityTransientField @JsonIgnore ContentLoader newContent)
        implements EmbeddableBinaryBuilder.With {

    public static interface BinaryStoreContentLoader {
        InputStream get() throws BinaryStoreException;
    }

    public static interface ContentLoader extends Supplier<InputStream> {}

    public static EmbeddableBinary fromBinary(Binary binary) {
        return EmbeddableBinaryBuilder.builder()
                .id(binary.id())
                .contentType(binary.contentType())
                .size(binary.size())
                .created(binary.created())
                .modified(binary.modified())
                .build();
    }

    public static EmbeddableBinary newBinary(String contentType, ContentLoader newContent) {
        return EmbeddableBinaryBuilder.builder()
                .contentType(contentType)
                .newContent(newContent)
                .build();
    }
}
