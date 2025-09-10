package cloud.quinimbus.binarystore.persistence;

import cloud.quinimbus.persistence.api.entity.PropertyContext;
import java.util.Set;

public record EmbeddableBinaryContext(Set<String> allowedContentTypes) implements PropertyContext {}
