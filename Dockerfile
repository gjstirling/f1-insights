# Use OpenJDK 11 as the base image
FROM openjdk:11-jdk

# Set the working directory && Copy the application files to the container
WORKDIR /app
COPY target/universal/stage /app

# Expose the port your application runs on
EXPOSE 8080

# Set the entry point to run the application - this is where the staged version of the app is located
CMD ["bin/f1-insights"]

# Set $SECRET as an argument in the terminal then pass this to our application (Scala Play Specific)
ARG SECRET
ENV APPLICATION_SECRET=$SECRET