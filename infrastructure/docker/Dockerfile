FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# If mvnw was created on Windows, it might have CRLF line endings, which can break execution in Linux (Docker)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
# Зависимости тянем до копирования исходников — слой кэшируется, пока не меняется pom.xml,
# поэтому правки кода НЕ приводят к повторной загрузке зависимостей.
RUN ./mvnw dependency:go-offline -B
COPY ./src ./src
# -DskipTests не пропускает компиляцию тестов; для Docker достаточно package без test-compile.
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /opt/app/target/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
