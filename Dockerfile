FROM arm64v8/eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
# skip tests надо, т.к. мавен начинает чекать на тесты до запуска приложения
RUN ./mvnw clean install -Dmaven.test.skip=true

FROM arm64v8/eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /opt/app/target/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
