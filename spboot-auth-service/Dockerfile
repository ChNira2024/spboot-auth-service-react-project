# ---------- STAGE 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first (for caching dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# ---------- STAGE 2: Run ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from builder
COPY --from=builder /app/target/*.jar spboot-auth-service.jar

# Expose port
EXPOSE 8085

# Run application
ENTRYPOINT ["java","-jar","spboot-auth-service.jar"]