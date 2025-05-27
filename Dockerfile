FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /opt/app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Кэшируем зависимости Maven
RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw dependency:go-offline -B \
    -Dmaven.wagon.http.connectionTimeout=60000 \
    -Dmaven.wagon.http.readTimeout=60000

COPY ./src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    ./mvnw clean install -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /opt/app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
