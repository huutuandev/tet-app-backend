# ========== BUILD ==========
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ========== RUN ==========
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# ENTRYPOINT đúng cú pháp: array JSON, mỗi phần tử " "
ENTRYPOINT ["java", "-Xms128m", "-Xmx384m", "-XX:MaxMetaspaceSize=128m", "-XX:+UseG1GC", "-jar", "app.jar"]