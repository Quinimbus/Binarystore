package cloud.quinimbus.binarystore.core.file;

import cloud.quinimbus.binarystore.api.Binary;
import cloud.quinimbus.binarystore.api.BinaryStoreException;
import cloud.quinimbus.binarystore.api.storage.BinaryStorage;
import cloud.quinimbus.tools.throwing.ThrowingMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

public class FileBasedBinaryStorage implements BinaryStorage {

    private final Path rootPath;

    private final ThrowingMap<String, FileBasedBinaryStorage, BinaryStoreException> subStorages;

    private final ObjectMapper mapper;

    public FileBasedBinaryStorage(Path rootPath) throws BinaryStoreException {
        this.rootPath = rootPath;
        this.subStorages = ThrowingMap.of(new LinkedHashMap<>(), BinaryStoreException.class);
        if (!Files.exists(this.rootPath)) {
            try {
                Files.createDirectories(this.rootPath);
            } catch (IOException ex) {
                throw new BinaryStoreException(
                        "Cannot create configured directory %s".formatted(this.rootPath.toString()), ex);
            }
        }
        if (!Files.isDirectory(this.rootPath)) {
            throw new BinaryStoreException(
                    "Configured directory %s is not a directory".formatted(this.rootPath.toString()));
        }
        this.mapper = new ObjectMapper();
    }

    private Path directoryPath(String id) throws IOException, BinaryStoreException {
        var path = this.rootPath.resolve(id.substring(0, 1)).resolve(id.substring(1, 2));
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                throw new BinaryStoreException("Cannot create binary directory %s".formatted(path.toString()), ex);
            }
        }
        if (!Files.isDirectory(path)) {
            throw new BinaryStoreException("Binary directory %s is not a directory".formatted(path.toString()));
        }
        return path;
    }

    private Path binaryPath(String id) throws IOException, BinaryStoreException {
        return this.directoryPath(id).resolve(id);
    }

    private Path metaPath(String id) throws IOException, BinaryStoreException {
        return this.directoryPath(id).resolve(id + ".json");
    }

    private void idValid(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("binary id may not be null or empty");
        }
    }

    private void inputStreamValid(InputStream is) {
        if (is == null) {
            throw new IllegalArgumentException("InputStream may not be null");
        }
    }

    private void contentTypeValid(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("content type may not be null or empty");
        }
    }

    @Override
    public BinaryStorage subStorage(String... ident) throws BinaryStoreException {
        return switch (ident.length) {
            case 0 -> this;
            case 1 -> this.subStorages.computeIfAbsent(
                    ident[0], i -> new FileBasedBinaryStorage(this.rootPath.resolve(i)));
            default -> this.subStorages
                    .computeIfAbsent(ident[0], i -> new FileBasedBinaryStorage(this.rootPath.resolve(i)))
                    .subStorage(Arrays.copyOfRange(ident, 1, ident.length));
        };
    }

    @Override
    public Optional<Binary> load(String id) throws BinaryStoreException {
        idValid(id);
        try {
            var binaryPath = this.binaryPath(id);
            var metaPath = this.metaPath(id);
            if (!Files.exists(metaPath)) {
                return Optional.empty();
            }
            try (var is = Files.newInputStream(metaPath)) {
                var meta = this.mapper.readValue(is, BinaryMeta.class);
                var attr = Files.readAttributes(binaryPath, BasicFileAttributes.class);
                return Optional.of(new Binary(
                        id,
                        meta.contentType(),
                        attr.size(),
                        attr.creationTime().toInstant(),
                        attr.lastModifiedTime().toInstant(),
                        meta.hash()));
            }
        } catch (IOException ex) {
            throw new BinaryStoreException("Cannot load the binary data for %s".formatted(id), ex);
        }
    }

    @Override
    public InputStream read(String id) throws BinaryStoreException {
        idValid(id);
        try {
            return Files.newInputStream(this.binaryPath(id));
        } catch (IOException ex) {
            throw new BinaryStoreException("Cannot read binary file", ex);
        }
    }

    @Override
    public Binary save(InputStream is, String contentType) throws BinaryStoreException {
        inputStreamValid(is);
        contentTypeValid(contentType);
        try {
            String id;
            Path binaryPath;
            Path metaPath;
            do {
                id = UUID.randomUUID().toString();
                binaryPath = this.binaryPath(id);
                metaPath = this.metaPath(id);
            } while (Files.exists(binaryPath) || Files.exists(metaPath));
            return this.save(id, binaryPath, metaPath, is, contentType);
        } catch (IOException ex) {
            throw new BinaryStoreException("Cannot generate the binary path", ex);
        }
    }

    @Override
    public Binary save(String id, InputStream is, String contentType) throws BinaryStoreException {
        idValid(id);
        inputStreamValid(is);
        contentTypeValid(contentType);
        try {
            return this.save(id, this.binaryPath(id), this.metaPath(id), is, contentType);
        } catch (IOException ex) {
            throw new BinaryStoreException("Cannot generate the binary path", ex);
        }
    }

    private Binary save(String id, Path binaryPath, Path metaPath, InputStream is, String contentType)
            throws BinaryStoreException {
        try (var dis = new DigestInputStream(is, MessageDigest.getInstance("SHA-1"))) {
            Files.copy(dis, binaryPath, StandardCopyOption.REPLACE_EXISTING);
            if (!Files.exists(metaPath)) {
                Files.createFile(metaPath);
            }
            var hash = Base64.getEncoder().encodeToString(dis.getMessageDigest().digest());
            try (var mos = Files.newOutputStream(metaPath)) {
                this.mapper.writeValue(mos, new BinaryMeta(contentType, hash));
            }
            var attr = Files.readAttributes(binaryPath, BasicFileAttributes.class);
            return new Binary(
                    id,
                    contentType,
                    attr.size(),
                    attr.creationTime().toInstant(),
                    attr.lastModifiedTime().toInstant(),
                    hash);
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new BinaryStoreException("Cannot save a binary", ex);
        }
    }

    @Override
    public void remove(String id) throws BinaryStoreException {
        idValid(id);
        try {
            Files.deleteIfExists(this.binaryPath(id));
            Files.deleteIfExists(this.metaPath(id));
        } catch (IOException ex) {
            throw new BinaryStoreException("", ex);
        }
    }
}
