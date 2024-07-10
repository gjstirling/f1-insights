# Use OpenJDK 11 as the base image
FROM openjdk:11-jdk

# Set the working directory && Copy the application files to the container
WORKDIR /app
COPY target/universal/stage /app

# Expose the port your application runs on
EXPOSE 8080

# Set a default value for PORT environment variable
ENV PORT=8080

# Set the entry point to run the application
CMD ["bin/f1-insights", "-Dhttp.port=8080"]

# Set $SECRET as an argument in the terminal then pass this to our application (Scala Play Specific)
ARG SECRET
ENV APPLICATION_SECRET=$SECRET