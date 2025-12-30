package com.dfac.visualizer.service;

import com.dfac.visualizer.model.UploadedFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");
    private final Map<String, UploadedFile> filesById = new ConcurrentHashMap<>();
    private final GrpcClientService grpcClientService;

    public FileStorageService(GrpcClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public UploadedFile uploadFile(MultipartFile file, String userId, String username) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            originalFileName = "unnamed_file";
        }

        // Generate unique storage name
        String storageFileName = System.currentTimeMillis() + "_" + originalFileName;
        Path targetPath = uploadDir.resolve(storageFileName);

        // Save file to disk
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Create file metadata
        UploadedFile uploadedFile = new UploadedFile(
                originalFileName,
                targetPath.toString(),
                userId,
                username);

        // Request lock from Replica Manager
        try {
            var response = grpcClientService.requestLockForUser(uploadedFile.getId(), userId, username);
            if (response.getStatus().name().equals("GRANTED")) {
                uploadedFile.setLocked(true);
                uploadedFile.setLockLamportTime(response.getLamportTime());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not acquire lock from RM: " + e.getMessage());
            // Still mark as locked locally
            uploadedFile.setLocked(true);
        }

        filesById.put(uploadedFile.getId(), uploadedFile);
        return uploadedFile;
    }

    public List<UploadedFile> getAllFiles() {
        return new ArrayList<>(filesById.values());
    }

    public List<UploadedFile> getFilesOwnedBy(String userId) {
        return filesById.values().stream()
                .filter(f -> f.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<UploadedFile> getFile(String fileId) {
        return Optional.ofNullable(filesById.get(fileId));
    }

    public boolean releaseLock(String fileId, String userId) {
        UploadedFile file = filesById.get(fileId);
        if (file == null) {
            System.out.println("DEBUG: releaseLock failed - file not found: " + fileId);
            return false;
        }

        System.out.println("DEBUG: releaseLock - fileId=" + fileId + ", requestingUserId=" + userId + ", fileOwnerId="
                + file.getOwnerId());

        // Only owner can release
        if (!file.getOwnerId().equals(userId)) {
            System.out.println("DEBUG: releaseLock failed - user " + userId + " is not the owner (owner is: "
                    + file.getOwnerId() + ")");
            return false;
        }

        // Release lock from Replica Manager
        try {
            var response = grpcClientService.releaseLockForUser(fileId, userId, file.getOwnerUsername());
            System.out.println("DEBUG: gRPC response status: " + response.getStatus().name());
            if (response.getStatus().name().equals("GRANTED")) {
                file.setLocked(false);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not release lock from RM: " + e.getMessage());
            // Release locally anyway
            file.setLocked(false);
            return true;
        }

        // If gRPC returns DENIED but we are the owner, release locally anyway
        file.setLocked(false);
        return true;
    }

    public boolean deleteFile(String fileId, String userId) {
        UploadedFile file = filesById.get(fileId);
        if (file == null || !file.getOwnerId().equals(userId)) {
            return false;
        }

        // Release lock first if held
        if (file.isLocked()) {
            releaseLock(fileId, userId);
        }

        // Delete from disk
        try {
            Files.deleteIfExists(Paths.get(file.getStoragePath()));
        } catch (IOException e) {
            System.err.println("Warning: Could not delete file from disk: " + e.getMessage());
        }

        filesById.remove(fileId);
        return true;
    }

    /**
     * Request access to a file. A user can only get access if the file is currently
     * unlocked.
     */
    public AccessResult requestAccess(String fileId, String userId, String username) {
        UploadedFile file = filesById.get(fileId);

        if (file == null) {
            return new AccessResult(false, "File not found");
        }

        // Check if file is currently locked
        if (file.isLocked()) {
            return new AccessResult(false,
                    "Cannot request access. File is currently locked by " + file.getOwnerUsername());
        }

        // File is unlocked, try to acquire lock from Replica Manager
        try {
            var response = grpcClientService.requestLockForUser(fileId, userId, username);
            if (response.getStatus().name().equals("GRANTED")) {
                // Update file ownership and lock status
                file.setOwnerId(userId);
                file.setOwnerUsername(username);
                file.setLocked(true);
                file.setLockLamportTime(response.getLamportTime());
                return new AccessResult(true, "Lock acquired successfully");
            } else {
                return new AccessResult(false, "Lock request was denied by the Replica Manager");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not acquire lock from RM: " + e.getMessage());
            // For local testing, grant lock anyway
            file.setOwnerId(userId);
            file.setOwnerUsername(username);
            file.setLocked(true);
            return new AccessResult(true, "Lock acquired locally");
        }
    }

    /**
     * Simple result class for access request operations
     */
    public static class AccessResult {
        private final boolean success;
        private final String message;

        public AccessResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
