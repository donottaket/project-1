# Проект первого модуля

## Структура проекта

Сервисы развертываются в Docker-контейнерах вместе с Kafka-кластером в режиме KRaft.

| Сервис                                             | Описание                                                                                                 |
|----------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| [Producer](producer)                               | Сервис-продюсер, отправляющий сообщения в Kafka. Публикует сообщения с интервалом от 100 мс. до 1000 мс. |
| [Batch Message Consumer](batch-message-consumer)   | Сервис-консьюмер, получающий сообщения из Kafka пачками. Обрабатывает минимум 10 сообщений за раз.       |
| [Single Message Consumer](single-message-consumer) | Сервис-консьюмер, обрабатывающий сообщения по одному.                                                    |

| Конфигурационный файл                             | Описание                                                                    |
|---------------------------------------------------|-----------------------------------------------------------------------------|
| [Kafka cluster](kafka-cluster-docker-compose.yml) | Конфигурация кластера Kafka. Кластер состоит из 3 брокеров и 1 контроллера. |
| [Java services](java-services-docker-compose.yml) | Конфигурация для запуска сервисов.                                          |
| [Dockerfile](Dockerfile)                          | Единый Dockerfile для всех сервисов.                                        |

## Описание классов

### Producer

Расположен в `kz.yandex_practicum.kafka.producer`.

| Класс              | Описание                                                   |
|--------------------|------------------------------------------------------------|
| `ProducerLauncher` | Основной класс, который публикует сообщения в Kafka-топик. |
| `JsonSerializer`   | Сериализует сообщения перед отправкой.                     |
| `Message`          | Модель сообщения.                                          |

### Batch Message Consumer

Расположен в `kz.yandex_practicum.kafka.batch_message_consumer`.

| Класс                          | Описание                                   |
|--------------------------------|--------------------------------------------|
| `BatchMessageConsumerLauncher` | Получает и обрабатывает сообщения пачками. |
| `JsonDeserializer`             | Десериализует сообщения.                   |
| `Message`                      | Модель сообщения.                          |

### Single Message Consumer

Расположен в `kz.yandex_practicum.kafka.single_message_consumer`.

| Класс                           | Описание                                           |
|---------------------------------|----------------------------------------------------|
| `SingleMessageConsumerLauncher` | Получает и обрабатывает каждое сообщение отдельно. |
| `JsonDeserializer`              | Десериализует сообщения.                           |
| `Message`                       | Модель сообщения.                                  |

## Инструкция по запуску

1. **Запустить Kafka-кластер:**

   ```sh
   docker-compose -f kafka-cluster-docker-compose.yml up -d
   ```

2. **Создать Kafka-топик:**

   ```sh
   docker exec -it kafka-broker-0 kafka-topics.sh --create --topic project-1-topic --bootstrap-server kafka-broker-0:9092 --partitions 3 --replication-factor 2
   ```

3. **Запустить сервисы:**

   ```sh
   docker-compose -f java-services-docker-compose.yml up --build -d
   ```

## Проверка работы

1. Открыть Kafka UI на `http://localhost:8080` и убедиться, что сообщения поступают в `project-1-topic`.

2. Убедиться, что сообщения корректно обрабатываются и коммитятся. Проверить логи консьюмеров:
   ```sh
   docker logs -f project-1-batch-message-consumer-1
   ```

   ```sh
   docker logs -f project-1-batch-message-consumer-2
   ```

   ```sh
   docker logs -f project-1-single-message-consumer-1
   ```

   ```sh
   docker logs -f project-1-single-message-consumer-2
   ```

## Завершение работы

Чтобы остановить все сервисы, выполните:

   ```sh
      docker-compose -f java-services-docker-compose.yml down
   ```

   ```sh
      docker-compose -f kafka-cluster-docker-compose.yml down
   ```