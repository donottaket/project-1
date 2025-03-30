# Универсальный Dockerfile для сборки и запуска микросервисов на Java с использованием Gradle

# Этап сборки Gradle-проекта
FROM gradle:8.13-jdk23 AS builder

WORKDIR /builder

COPY settings.gradle build.gradle ./

COPY producer/src/main ./producer/src/main
COPY producer/build.gradle ./producer

COPY single-message-consumer/src/main ./single-message-consumer/src/main
COPY single-message-consumer/build.gradle ./single-message-consumer

COPY batch-message-consumer/src/main ./batch-message-consumer/src/main
COPY batch-message-consumer/build.gradle ./batch-message-consumer

# Сборка всех модулей
RUN gradle --parallel --no-daemon clean build -x test

# Этап исполнения
FROM eclipse-temurin:23-jre

WORKDIR /app
ARG SERVICE_NAME

# Копируем скомпилированный JAR из builder-этапа
COPY --from=builder /builder/$SERVICE_NAME/build/libs/*.jar app.jar

# Запуск сервиса
CMD ["java", "-jar", "app.jar"]