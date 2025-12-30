package com.dfac.visualizer.controller;

import com.dfac.visualizer.model.UploadedFile;
import com.dfac.visualizer.service.FileStorageService;
import com.dfac.visualizer.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserService userService;

    public FileController(FileStorageService fileStorageService, UserService userService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            var user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UploadedFile uploadedFile = fileStorageService.uploadFile(
                    file,
                    user.getId(),
                    user.getUsername());

            response.put("success", true);
            response.put("message", "File uploaded and locked successfully");
            response.put("fileId", uploadedFile.getId());
            response.put("fileName", uploadedFile.getOriginalFileName());
            response.put("locked", uploadedFile.isLocked());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listFiles(
            @AuthenticationPrincipal UserDetails userDetails) {

        var currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
        String currentUserId = currentUser != null ? currentUser.getId() : "";

        List<Map<String, Object>> files = fileStorageService.getAllFiles().stream()
                .map(file -> {
                    Map<String, Object> fileMap = new HashMap<>();
                    fileMap.put("id", file.getId());
                    fileMap.put("fileName", file.getOriginalFileName());
                    fileMap.put("owner", file.getOwnerUsername());
                    fileMap.put("locked", file.isLocked());
                    fileMap.put("lockTime", file.getLockLamportTime());
                    fileMap.put("uploadTime", file.getUploadTime().toString());
                    fileMap.put("isOwner", file.getOwnerId().equals(currentUserId));
                    return fileMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    @PostMapping("/{fileId}/release")
    public ResponseEntity<Map<String, Object>> releaseLock(
            @PathVariable String fileId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        var user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean released = fileStorageService.releaseLock(fileId, user.getId());

        if (released) {
            response.put("success", true);
            response.put("message", "Lock released successfully");
        } else {
            response.put("success", false);
            response.put("message", "Failed to release lock. You may not own this file.");
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable String fileId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        var user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean deleted = fileStorageService.deleteFile(fileId, user.getId());

        if (deleted) {
            response.put("success", true);
            response.put("message", "File deleted successfully");
        } else {
            response.put("success", false);
            response.put("message", "Failed to delete file. You may not own this file.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fileId}/request-access")
    public ResponseEntity<Map<String, Object>> requestAccess(
            @PathVariable String fileId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        var user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var result = fileStorageService.requestAccess(fileId, user.getId(), user.getUsername());

        if (result.isSuccess()) {
            response.put("success", true);
            response.put("message", "Access granted! You now have the lock on this file.");
        } else {
            response.put("success", false);
            response.put("message", result.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
