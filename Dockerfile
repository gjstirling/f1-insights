# Stage 1: Build the project with sbt
FROM openjdk:11-jdk AS builder

RUN apt-get update && apt-get install -y curl gnupg

RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | apt-key add && \
    apt-get update && apt-get install -y sbt

WORKDIR /app
COPY . .
RUN sbt stage

FROM openjdk:11-jdk
WORKDIR /app
COPY --from=builder /app/target/universal/stage /app

EXPOSE 8080
ENV PORT=8080

CMD ["bin/f1-insights", "-Dhttp.port=8080"]
