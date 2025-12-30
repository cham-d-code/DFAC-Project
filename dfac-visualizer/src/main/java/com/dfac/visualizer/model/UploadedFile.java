package com.dfac.visualizer.model;

import java.time.Instant;
import java.util.UUID;

public class UploadedFile {
    private String id;
    private String originalFileName;
    private String storagePath;
    private String ownerId;
    private String ownerUsername;
    private Instant uploadTime;
    private boolean locked;
    private long lockLamportTime;

    public UploadedFile() {
        this.id = UUID.randomUUID().toString();
        this.uploadTime = Instant.now();
        this.locked = false;
    }

    public UploadedFile(String originalFileName, String storagePath, String ownerId, String ownerUsername) {
        this.id = UUID.randomUUID().toString();
        this.originalFileName = originalFileName;
        this.storagePath = storagePath;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.uploadTime = Instant.now();
        this.locked = true; // Auto-locked on upload
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Instant getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Instant uploadTime) {
        this.uploadTime = uploadTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public long getLockLamportTime() {
        return lockLamportTime;
    }

    public void setLockLamportTime(long lockLamportTime) {
        this.lockLamportTime = lockLamportTime;
    }
}
