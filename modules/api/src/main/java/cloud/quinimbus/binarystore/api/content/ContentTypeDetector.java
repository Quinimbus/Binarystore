package cloud.quinimbus.binarystore.api.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface ContentTypeDetector {

    Optional<String> detect(InputStream inputStream, String givenContentType) throws IOException;
}
