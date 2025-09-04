package com.prashantlabs.documents.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class DocumentStorageService {
    private final Path root;

    public DocumentStorageService(@Value("${app.storage.dir}") String dir) {
        this.root = Path.of(dir);
    }

    public String save(MultipartFile file) {
        try {
            Files.createDirectories(root);
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String name = UUID.randomUUID() + (ext != null ? ("." + ext) : "");
            Path dest = root.resolve(name);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("store failed", e);
        }
    }
}
