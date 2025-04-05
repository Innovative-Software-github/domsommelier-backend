FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY ./src ./src
# If mvnw was created on Windows, it might have CRLF line endings, which can break execution in Linux (Docker)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline
# skip tests надо, т.к. мавен начинает чекать на тесты до запуска приложения
RUN ./mvnw clean install -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /opt/app/target/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
