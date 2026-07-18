# syntax=docker/dockerfile:1

# ---------- Étape build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache des dépendances (couche séparée du code source)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Étape runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Utilisateur non-root
RUN useradd -r -u 1001 samabank
USER samabank

COPY --from=build /app/target/samabank-backend-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
