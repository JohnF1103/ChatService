# Step 1: Use a lightweight Java 17 image
FROM openjdk:17-jdk-slim

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy the built JAR from Maven target folder into the container
COPY target/LLM_SVC-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose port 8080
EXPOSE 8080

# Step 5: Define the default command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
