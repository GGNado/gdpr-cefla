# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
# Download dependencies first (cache layer)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Variabili d'ambiente sovrascritte a runtime via docker-compose
ENV SPRING_DATASOURCE_URL=jdbc:sqlserver://host.docker.internal:1433;databaseName=cefladb_iot;encrypt=false;
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=Password123!

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
