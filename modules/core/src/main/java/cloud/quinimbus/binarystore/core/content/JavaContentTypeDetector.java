package cloud.quinimbus.binarystore.core.content;

import cloud.quinimbus.binarystore.api.content.ContentTypeDetector;
import cloud.quinimbus.common.annotations.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Provider(id = "java", name = "Java NIO based content type detector", priority = 10)
public class JavaContentTypeDetector implements ContentTypeDetector {

    @Override
    public Optional<String> detect(InputStream inputStream, String givenContentType) throws IOException {
        if (inputStream.markSupported()) {
            var tmpFile = Files.createTempFile("qn-typedetect-", "");
            inputStream.mark(Integer.MAX_VALUE);
            Files.copy(inputStream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
            inputStream.reset();
            var contentType = Files.probeContentType(tmpFile);
            Files.delete(tmpFile);
            return Optional.ofNullable(contentType);
        }
        return Optional.empty();
    }
}
