package com.dfac.replica_manager;

import com.dfac.replica_manager.service.LockServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.apache.curator.test.TestingServer;

import java.io.IOException;

public class ReplicaManagerServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Configuration from environment variables
        String portEnv = System.getenv("GRPC_PORT");
        int port = (portEnv != null && !portEnv.isEmpty()) ? Integer.parseInt(portEnv) : 50051;

        String zkConnectString = System.getenv("ZOOKEEPER_CONNECT");
        TestingServer zkServer = null;

        if (zkConnectString == null || zkConnectString.isEmpty()) {
            System.out.println("No ZOOKEEPER_CONNECT env var found. Starting embedded Zookeeper server...");
            try {
                zkServer = new TestingServer(2181, true);
                zkConnectString = zkServer.getConnectString();
                System.out.println("Embedded Zookeeper started at " + zkConnectString);
            } catch (Exception e) {
                System.err.println("Failed to start embedded Zookeeper: " + e.getMessage());
                // Fallback or exit? If we can't start ZK, we probably can't run.
                // But maybe port 2181 is taken. Let's try random port if 2181 fails?
                // For now, let's just let it throw or print.
                // Actually TestingServer() without arguments picks a random port.
                // Let's stick to 2181 for consistency with default, but if it fails, maybe we
                // should have let it pick.
                // But the user might expect 2181.
                throw new RuntimeException("Could not start embedded Zookeeper", e);
            }
        }

        System.out.println("Starting Replica Manager on port " + port);
        System.out.println("Connecting to ZooKeeper at " + zkConnectString);

        // Initialize Curator Client
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zkConnectString,
                new ExponentialBackoffRetry(1000, 3));
        client.start();

        // Start gRPC Server
        Server server = ServerBuilder.forPort(port)
                .addService(new LockServiceImpl(client))
                .build()
                .start();

        System.out.println("Replica Manager started, listening on " + port);

        final TestingServer finalZkServer = zkServer;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            client.close();
            if (finalZkServer != null) {
                try {
                    finalZkServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.err.println("*** server shut down");
        }));

        server.awaitTermination();
    }
}
