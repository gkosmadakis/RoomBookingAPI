# Use official maven image to build the project
FROM openjdk:21-bullseye AS build
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy the project files and build the application
COPY . .
RUN mvn clean package -DskipTests

# Use openjdk to run the app
FROM amazoncorretto:21
COPY --from=build /app/target/user-management-0.0.1-SNAPSHOT.jar /user-management.jar
ENTRYPOINT ["java", "-jar", "/user-management.jar"]
# Expose the port the app runs on
EXPOSE 8081