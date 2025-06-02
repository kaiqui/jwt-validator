FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app


RUN apt-get update && apt-get install -y curl && \
    curl -Lo dd-java-agent.jar https://dtdg.co/latest-java-tracer && \
    chmod 644 dd-java-agent.jar && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV DD_SERVICE=app-jwt-validator
ENV DD_ENV=prod
ENV DD_VERSION=1.0.0
ENV DD_AGENT_HOST=datadog-agent
ENV DD_LOGS_INJECTION=true
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/dd-java-agent.jar -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java", "-jar", "app.jar"]
