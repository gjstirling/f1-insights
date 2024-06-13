# Use OpenJDK 11 as the base image
FROM openjdk:11-jdk

# Set the working directory
WORKDIR /app

# Copy the application files to the container
COPY target/universal/stage /app

# Expose the port your application runs on
EXPOSE 8080

# Set the entry point to run the application
CMD ["bin/f1-insights"]

ARG SECRET
ENV APPLICATION_SECRET=$SECRET