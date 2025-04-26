# Build stage using Maven
FROM maven:3.8.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY lib ./lib
COPY src ./src
RUN mvn install:install-file -Dfile=/app/lib/swisseph.jar -DgroupId=org.astrologer -DartifactId=swisseph -Dversion=2.10.03 -Dpackaging=jar
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 