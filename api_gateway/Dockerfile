# Stage 1: Build the application using Gradle
FROM gradle:7.4.2-jdk18 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY . .

# Ensure gradlew is executable
RUN chmod +x gradlew

# Build the project using Gradle
RUN ./gradlew build

# Stage 2: Run the application using OpenJDK
FROM openjdk:17-jdk-slim

# Set a volume for temporary files
VOLUME /tmp

# Copy the built JAR file from the first stage
COPY --from=build /app/build/libs/api_gateway-0.0.1-SNAPSHOT.jar app.jar

# Specify the entry point command
ENTRYPOINT ["java","-jar","/app.jar"]

