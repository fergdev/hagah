# ---- Build stage ----
FROM gradle:8.5.0-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy source
COPY . .

# Build the fat JAR
RUN ./gradlew :server:shadowJar --no-daemon


# ---- Runtime stage ----
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy only the fat JAR
COPY --from=builder /app/server/build/libs/hagah-server.jar app.jar

# Expose the port (Railway will assign it dynamically)
EXPOSE 8080

# Start the server (reads PORT from env var)
CMD ["java", "-jar", "app.jar"]
