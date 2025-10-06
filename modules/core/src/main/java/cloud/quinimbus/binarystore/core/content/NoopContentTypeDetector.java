package cloud.quinimbus.binarystore.core.content;

import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.common.annotations.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Provider(id = "noop", name = "No operation content type detector", priority = 0)
public class NoopContentTypeDetector implements ContentTypeDetector {

    @Override
    public Optional<String> detect(InputStream inputStream, String givenContentType) throws IOException {
        return Optional.empty();
    }
}
