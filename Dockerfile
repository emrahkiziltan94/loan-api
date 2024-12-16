FROM maven:3.8.7-openjdk-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=UTC

WORKDIR /app

COPY --from=build /app/target/loan-api-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
