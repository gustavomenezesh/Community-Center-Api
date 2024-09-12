FROM ubuntu:22.04

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src /app/src

RUN mvn clean package

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
