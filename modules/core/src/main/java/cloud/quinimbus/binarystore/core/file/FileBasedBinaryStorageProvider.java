package cloud.quinimbus.binarystore.core.file;

import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import cloud.quinimbus.common.annotations.Provider;
import cloud.quinimbus.config.api.ConfigNode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Provider(name = "Filesystem based binary storage provider", alias = "file", priority = 0)
public class FileBasedBinaryStorageProvider implements BinaryStorageProvider<FileBasedBinaryStorage> {

    @Override
    public FileBasedBinaryStorage createStorage(Map<String, Object> params) throws BinaryStoreException {
        var rootPath = Optional.ofNullable(params.get("rootPath"))
                .filter(p -> p instanceof String)
                .map(p -> Paths.get((String) p))
                .orElseThrow(() -> new BinaryStoreException("Missing or invalid parameter: rootPath"));
        return new FileBasedBinaryStorage(rootPath);
    }

    @Override
    public FileBasedBinaryStorage createStorage(ConfigNode config) throws BinaryStoreException {
        var rootPath = config.asString("rootPath")
                .map(p -> Paths.get((String) p))
                .orElseThrow(() -> new BinaryStoreException("Missing configuration value: rootPath"));
        return new FileBasedBinaryStorage(rootPath);
    }

    public FileBasedBinaryStorage createStorage(Path rootPath) throws BinaryStoreException {
        return new FileBasedBinaryStorage(rootPath);
    }
}
