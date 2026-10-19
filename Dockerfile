# Stage 1: Build the application using Gradle
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and build definitions
COPY gradlew .
COPY gradlew.bat .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src ./src

# Make gradle wrapper executable and build bootJar
RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create folders for uploads and runtime logs
RUN mkdir -p /uploads/logos /app/logs

# Copy executable jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose application port
EXPOSE 9081

# Set default active profile to dev
ENV SPRING_PROFILES_ACTIVE=dev

# Start Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
