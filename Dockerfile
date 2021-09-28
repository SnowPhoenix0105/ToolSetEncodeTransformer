# syntax=docker/dockerfile:1
FROM openjdk:8-jre
WORKDIR /usr/local
COPY ./target/ToolSetEncodeTransformer.jar /usr/local/ToolSetEncodeTransformer.jar
# ENTRYPOINT ["java", "-jar", "/usr/local/ToolSetEncodeTransformer.jar"]
