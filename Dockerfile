# Stage 1: Build the application
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app
COPY . .

# Dar permisos de ejecución a gradlew
RUN chmod +x gradlew

# Construir la aplicación
RUN ./gradlew clean build -x test

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR generado
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
