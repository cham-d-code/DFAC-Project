# Stage 1: Build
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Copy settings and root build file
COPY settings.gradle build.gradle ./

# Copy modules
COPY dfac-common ./dfac-common
COPY dfac-replica-manager ./dfac-replica-manager
COPY dfac-worker ./dfac-worker

# Build the Worker module
RUN gradle :dfac-worker:shadowJar --no-daemon

# Stage 2: Runtime
FROM openjdk:17-jre-slim
WORKDIR /app

# Copy the fat jar from the builder stage
COPY --from=builder /app/dfac-worker/build/libs/*-all.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
