FROM gradle:7.4.2-jdk18 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY . .

# Navigate to the directory containing the gradlew script
WORKDIR /app/crm

# Ensure gradlew is executable
RUN chmod +x gradlew

# Build the project using bootBuildImage
RUN ./gradlew bootBuildImage

# Since bootBuildImage creates a separate image, 
# we need to extract it from the Docker daemon.
FROM scratch AS extract
COPY --from=build /app/crm/build/image/image.tar /image.tar

# Use a lighter image for the runtime with JDK 18
FROM openjdk:18-jdk-slim

# Extract the application image
COPY --from=extract /image.tar /image.tar
RUN tar -xvf /image.tar --strip-components=1 -C /

# Command to run the application
ENTRYPOINT ["java", "-jar", "BOOT-INF/lib/spring-boot-loader.jar", "org.springframework.boot.loader.JarLauncher"]
