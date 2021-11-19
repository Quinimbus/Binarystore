package cloud.quinimbus.binarystore.api;

import java.time.Instant;

public record Binary(
        String id,
        String contentType,
        Long size,
        Instant created,
        Instant modified,
        String hash) {

}
