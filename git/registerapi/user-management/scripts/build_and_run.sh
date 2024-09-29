#!/bin/bash

# Build the Spring Boot application
echo "Building the project..."
docker rm -f user-management || true  # Force remove existing container
# Build the Docker image
docker build -t user-management-0.0.1-snapshot.jar .

# Run the Docker container
docker run -p 8081:8081 user-management-0.0.1-snapshot.jar

echo "Application is running. Press [CTRL+C] to stop."
while true; do sleep 1000; done