# syntax=docker/dockerfile:1
FROM openjdk:8-jre
WORKDIR /usr/local
COPY ./target/ToolSetEncodeTransformer.jar /usr/local/ToolSetEncodeTransformer.jar
CMD ["java", "-jar", "/user/local/ToolSetEncodeTransformer.jar"]
