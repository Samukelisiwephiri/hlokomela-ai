package za.co.hlokomela.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import za.co.hlokomela.api.config.StorageProperties;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.exception.StorageException;

@Service
public class LocalFileStorageService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final StorageProperties properties;
    private Path root;

    public LocalFileStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    @SuppressWarnings("unused")
    void initialize() {
        try {
            root = Path.of(properties.getUploadDirectory()).toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new StorageException("Could not initialize the report upload directory", exception);
        }
    }

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (file.getSize() > properties.getMaxFileSizeBytes()) {
            throw new IllegalArgumentException("Photo must not exceed " + properties.getMaxFileSizeBytes() + " bytes");
        }
        String contentType = Objects.requireNonNullElse(file.getContentType(), "").toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, and WEBP images are accepted");
        }
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported image type");
        };
        String storageKey = UUID.randomUUID() + extension;
        Path destination = root.resolve(storageKey).normalize();
        if (!destination.getParent().equals(root)) {
            throw new StorageException("Invalid upload path");
        }
        try (var input = file.getInputStream()) {
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            return storageKey;
        } catch (IOException exception) {
            throw new StorageException("Could not save report photo", exception);
        }
    }

    public StoredFile load(String storageKey) {
        if (!StringUtils.hasText(storageKey)) {
            throw new ResourceNotFoundException("No photo is attached to this report");
        }
        Path file = root.resolve(storageKey).normalize();
        if (!file.getParent().equals(root) || !Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("Report photo was not found");
        }
        try {
            Resource resource = new UrlResource(Objects.requireNonNull(file.toUri()));
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Report photo was not found");
            }
            MediaType contentType = MediaTypeFactory.getMediaType(storageKey)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
            return new StoredFile(resource, contentType);
        } catch (IOException exception) {
            throw new StorageException("Could not read report photo", exception);
        }
    }

    public record StoredFile(Resource resource, MediaType contentType) { }
}
