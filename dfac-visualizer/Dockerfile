# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle settings.gradle ./
COPY dfac-common/build.gradle dfac-common/
COPY dfac-visualizer/build.gradle dfac-visualizer/

# Copy source files
COPY dfac-common/src dfac-common/src
COPY dfac-visualizer/src dfac-visualizer/src

# Build the application
RUN gradle :dfac-visualizer:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create uploads directory
RUN mkdir -p /app/uploads

# Copy the built jar
COPY --from=builder /app/dfac-visualizer/build/libs/*.jar app.jar

# Expose HTTP port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV REPLICA_MANAGER_HOST=dfac-rm-service
ENV REPLICA_MANAGER_PORT=50051
ENV SERVER_PORT=8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
