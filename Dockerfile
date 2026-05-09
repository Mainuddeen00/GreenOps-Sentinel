# Use an official, lightweight Java 21 image as the base
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled jar file from your Mac into the container
COPY target/sentinel-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your REST API uses
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]