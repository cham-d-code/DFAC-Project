# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle settings.gradle ./
COPY dfac-common/build.gradle dfac-common/
COPY dfac-replica-manager/build.gradle dfac-replica-manager/

# Copy source files
COPY dfac-common/src dfac-common/src
COPY dfac-replica-manager/src dfac-replica-manager/src

# Build the shadow jar (fat jar with all dependencies)
RUN gradle :dfac-replica-manager:shadowJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/dfac-replica-manager/build/libs/*-all.jar app.jar

# Expose gRPC port
EXPOSE 50051

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD nc -z localhost 50051 || exit 1

# Environment variables
ENV ZOOKEEPER_CONNECT=zookeeper:2181
ENV GRPC_PORT=50051

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
