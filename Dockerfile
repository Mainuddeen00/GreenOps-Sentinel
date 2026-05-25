# ==========================================
# Stage 1: Build the Java Application
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy the Maven wrapper and pom.xml first
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the wrapper executable
RUN chmod +x ./mvnw

# Copy the actual source code
COPY src src

# Compile the code into a .jar file INSIDE the container
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Run the Java Application
# ==========================================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy ONLY the built .jar file from the "builder" stage above
COPY --from=builder /app/target/sentinel-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]