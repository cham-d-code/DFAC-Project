package com.dfac.visualizer.controller;

import com.dfac.replica_manager.grpc.LockResponse;
import com.dfac.visualizer.service.GrpcClientService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VisualizerApiController {

    private final GrpcClientService grpcService;

    public VisualizerApiController(GrpcClientService grpcService) {
        this.grpcService = grpcService;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("rm1Healthy", grpcService.checkHealthRM1());
        status.put("rm2Healthy", grpcService.checkHealthRM2());

        // Mocking the lock table for visualization as we don't have a "getAllLocks" RPC
        // yet
        // In a real scenario, we would query the RM for its state.
        Map<String, Object> lock1 = new HashMap<>();
        lock1.put("fileName", "file1");
        lock1.put("holder", "user1");
        lock1.put("time", 1001);

        status.put("locks", java.util.Arrays.asList(lock1));

        return status;
    }

    @PostMapping("/request-lock")
    public String requestLock(@RequestParam String fileId, @RequestParam String userId) {
        LockResponse response = grpcService.requestLock(fileId, userId);
        return response.getStatus().name();
    }

    @PostMapping("/release-lock")
    public String releaseLock(@RequestParam String fileId, @RequestParam String userId) {
        LockResponse response = grpcService.releaseLock(fileId, userId);
        return response.getStatus().name();
    }
}
