package cloud.quinimbus.binarystore.persistence;

import cloud.quinimbus.common.annotations.Provider;
import cloud.quinimbus.persistence.api.entity.PropertyContext;
import cloud.quinimbus.persistence.api.records.RecordPropertyContextHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Provider(id = "embeddableBinary", name = "Context handler for embeddable binary record fields")
public class EmbeddableBinaryContextHandler implements RecordPropertyContextHandler {

    @Override
    public Optional<? extends PropertyContext> createContext(Field field) {
        var validateBinaryAnno = field.getAnnotation(ValidateBinary.class);
        if (validateBinaryAnno == null) {
            return Optional.empty();
        }
        return Optional.of(new EmbeddableBinaryContext(
                Arrays.stream(validateBinaryAnno.contentType()).collect(Collectors.toSet())));
    }
}
