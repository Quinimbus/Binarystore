package cloud.quinimbus.binarystore.tika;

import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.common.annotations.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.tika.Tika;

@Provider(id = "tika", name = "Apache Tika based content type detector", priority = 100)
public class TikaContentTypeDetector implements ContentTypeDetector {

    private final Tika tika;

    public TikaContentTypeDetector() {
        this.tika = new Tika();
    }

    @Override
    public Optional<String> detect(InputStream inputStream, String givenContentType) throws IOException {
        if (inputStream.markSupported()) {
            inputStream.mark(Integer.MAX_VALUE);
            var contentType = tika.detect(inputStream);
            inputStream.reset();
            return Optional.ofNullable(contentType);
        }
        return Optional.empty();
    }
}
