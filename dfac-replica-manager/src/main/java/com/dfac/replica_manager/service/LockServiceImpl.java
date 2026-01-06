package com.dfac.replica_manager.service;

import com.dfac.common.ClockService;
import com.dfac.replica_manager.grpc.LockRequest;
import com.dfac.replica_manager.grpc.LockResponse;
import com.dfac.replica_manager.grpc.LockServiceGrpc;
import com.dfac.replica_manager.grpc.LockStatus;
import io.grpc.stub.StreamObserver;

public class LockServiceImpl extends LockServiceGrpc.LockServiceImplBase {

    private final ClockService clockService;
    private final org.apache.curator.framework.CuratorFramework client;
    private final java.util.concurrent.ConcurrentHashMap<String, org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex> activeLocks;
    private final java.util.concurrent.ConcurrentHashMap<String, String> lockOwners;

    // Placeholder ACL and Token Store
    private final java.util.Map<String, String> fileAcl; // file_id -> authorized user_id
    private final java.util.Map<String, String> userTokens; // user_id -> valid auth_token

    public LockServiceImpl(org.apache.curator.framework.CuratorFramework client) {
        this.clockService = ClockService.getInstance();
        this.client = client;
        this.activeLocks = new java.util.concurrent.ConcurrentHashMap<>();
        this.lockOwners = new java.util.concurrent.ConcurrentHashMap<>();

        // Dynamic authorization - no hardcoded users/files
        // Authorization is now handled by the Visualizer service
        this.fileAcl = new java.util.concurrent.ConcurrentHashMap<>();
        this.userTokens = new java.util.concurrent.ConcurrentHashMap<>();
    }

    @Override
    public void requestLock(LockRequest request, StreamObserver<LockResponse> responseObserver) {
        // 1. Update local clock on receive
        long receivedTime = request.getLamportTime();
        clockService.updateOnReceive(receivedTime);

        String fileId = request.getFileId();
        String userId = request.getUserId();
        String lockPath = "/locks/" + fileId;

        LockStatus status;

        // Check if we already hold the lock for this user (Idempotency/Reentrancy)
        if (activeLocks.containsKey(fileId) && userId.equals(lockOwners.get(fileId))) {
            System.out.println("Lock already held by user: " + userId + " for file: " + fileId);
            status = LockStatus.GRANTED;
        } else if (activeLocks.containsKey(fileId)) {
            // Lock is held by someone else
            System.out.println("Lock denied - held by: " + lockOwners.get(fileId) + ", requested by: " + userId);
            status = LockStatus.DENIED;
        } else {
            org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex mutex = new org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex(
                    client, lockPath);

            try {
                // Attempt to acquire the lock with a timeout
                boolean acquired = mutex.acquire(5, java.util.concurrent.TimeUnit.SECONDS);

                if (acquired) {
                    // Lock acquired
                    activeLocks.put(fileId, mutex);
                    lockOwners.put(fileId, userId);
                    System.out.println("Lock GRANTED to user: " + userId + " for file: " + fileId);
                    status = LockStatus.GRANTED;
                } else {
                    System.out.println("Lock timeout for user: " + userId + " on file: " + fileId);
                    status = LockStatus.DENIED;
                }
            } catch (Exception e) {
                System.err.println("Error acquiring lock for file " + fileId + ": " + e.getMessage());
                status = LockStatus.SYSTEM_DOWN;
            }
        }

        // 2. Increment clock before sending response
        long responseTime = clockService.incrementAndGet();

        LockResponse response = LockResponse.newBuilder()
                .setStatus(status)
                .setLamportTime(responseTime)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Get all current locks - for visualization
    public java.util.Map<String, String> getAllLocks() {
        return new java.util.HashMap<>(lockOwners);
    }

    private boolean isAuthorized(String fileId, String userId, String authToken) {
        // Allow all requests - authorization handled by Visualizer
        return true;
    }

    @Override
    public void releaseLock(LockRequest request, StreamObserver<LockResponse> responseObserver) {
        // 1. Update local clock on receive
        long receivedTime = request.getLamportTime();
        clockService.updateOnReceive(receivedTime);

        String fileId = request.getFileId();
        String userId = request.getUserId();
        LockStatus status = LockStatus.DENIED;

        // Logic for unlocking
        if (activeLocks.containsKey(fileId)) {
            // Verify owner
            if (userId.equals(lockOwners.get(fileId))) {
                org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex mutex = activeLocks.get(fileId);
                try {
                    mutex.release();
                    activeLocks.remove(fileId);
                    lockOwners.remove(fileId);
                    status = LockStatus.GRANTED;
                } catch (Exception e) {
                    System.err.println("Error releasing lock for file " + fileId + ": " + e.getMessage());
                    status = LockStatus.SYSTEM_DOWN;
                }
            } else {
                // Not the owner
                status = LockStatus.DENIED;
            }
        } else {
            // Lock not found in memory (could be a zombie lock from a crashed session)
            String lockPath = "/locks/" + fileId;
            try {
                if (client.checkExists().forPath(lockPath) != null) {
                    System.out.println("Found zombie lock for file " + fileId + ". Force releasing.");
                    client.delete().forPath(lockPath);
                    status = LockStatus.GRANTED;
                } else {
                    // Lock really doesn't exist
                    status = LockStatus.DENIED;
                }
            } catch (Exception e) {
                System.err.println("Error checking/deleting zombie lock: " + e.getMessage());
                status = LockStatus.SYSTEM_DOWN;
            }
        }

        // 2. Increment clock before sending response
        long responseTime = clockService.incrementAndGet();

        LockResponse response = LockResponse.newBuilder()
                .setStatus(status)
                .setLamportTime(responseTime)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
