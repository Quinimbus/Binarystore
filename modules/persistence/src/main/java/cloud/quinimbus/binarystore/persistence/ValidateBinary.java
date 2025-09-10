package cloud.quinimbus.binarystore.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// This annotation can be used on embedded properties of type [EmbeddableBinary] to set validation configuration.
/// @since 0.2
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateBinary {

    /// @return The list of allowed content types for this binary. Wildcard (like `image/*`) is allowed.
    String[] contentType();
}
