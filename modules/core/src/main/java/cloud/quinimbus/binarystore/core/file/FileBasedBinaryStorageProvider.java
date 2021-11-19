package cloud.quinimbus.binarystore.core.file;

import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.binarystore.api.storage.BinaryStorageProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class FileBasedBinaryStorageProvider implements BinaryStorageProvider<FileBasedBinaryStorage> {

    @Override
    public FileBasedBinaryStorage createStorage(Map<String, Object> params) throws BinaryStoreException {
        var rootPath = Optional.ofNullable(params.get("rootPath"))
                .filter(p -> p instanceof String)
                .map(p -> Paths.get((String) p))
                .orElseThrow(() -> new BinaryStoreException("Missing or invalid parameter: rootPath"));
        return new FileBasedBinaryStorage(rootPath);
    }
    
    public FileBasedBinaryStorage createStorage(Path rootPath) throws BinaryStoreException {
        return new FileBasedBinaryStorage(rootPath);
    }
}
