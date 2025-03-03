# Stage 1: Build the application
FROM eclipse-temurin:17-jdk AS builder

# Set the working directory
WORKDIR /app

# Copy the application code
COPY . .

# Build the application (Gradle)
RUN ./gradlew clean build -x test

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app will run on
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"]
