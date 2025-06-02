FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Baixar o agente Datadog
ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-javaagent:/app/dd-java-agent.jar \
 -Ddd.agent.host=datadog-agent \
 -Ddd.service=app-jwt-validator \
 -Ddd.env=prod \
 -Ddd.version=1.0.0 \
 -Ddd.logs.injection=true \
 -XX:MaxRAMPercentage=75.0 \
 -XX:InitialRAMPercentage=50.0 \
 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java", "-jar", "app.jar"]
