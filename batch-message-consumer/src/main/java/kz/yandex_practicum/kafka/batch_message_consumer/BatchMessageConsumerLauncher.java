package kz.yandex_practicum.kafka.batch_message_consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Точка входа приложения для получения пакетов сообщений из Kafka.
 *
 * @author maenlest
 */
public class BatchMessageConsumerLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchMessageConsumerLauncher.class);

    private static final String TOPIC_NAME;
    private static final Properties PROPERTIES;

    static {
        TOPIC_NAME = "project-1-topic"; // Название топика

        PROPERTIES = new Properties();
        PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092"); // Адреса брокеров Kafka
        PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Десериализатор ключа
        PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName()); // Десериализатор значения
        PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "batch-group"); // Идентификатор группы потребителей
        PROPERTIES.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Авто-коммит отключен
        PROPERTIES.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "6000"); // Ждёт минимум 6000 байт
        PROPERTIES.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "65000"); // Ждёт максимум 65000 мс
    }

    public static void main(String[] args) {
        try (Consumer<String, Message> consumer = new KafkaConsumer<>(PROPERTIES)) {
            startConsumingMessages(consumer);
        }
    }

    /**
     * Запускает процесс получения сообщений из Kafka.
     *
     * @param consumer настроенный consumer для получения сообщений из Kafka.
     */
    public static void startConsumingMessages(Consumer<String, Message> consumer) {
        consumer.subscribe(List.of(TOPIC_NAME)); // Подписываемся на топик

        while (true) {
            ConsumerRecords<String, Message> records;

            try {
                records = consumer.poll(Duration.ofMillis(1_000L)); // Получаем сообщения из топика (может вернуть пустые данные, если нет готовых сообщений)
            } catch (Exception e) {
                LOGGER.error("Ошибка при получении сообщений: {}", e.getMessage());
                continue; // Пропускаем итерацию, если произошла ошибка
            }

            if (!records.isEmpty() && records.count() > 0) {
                LOGGER.info("Получено {} сообщений", records.count());

                for (var record : records) {
                    LOGGER.info("Получено сообщение: {}", record);
                }

                try {
                    consumer.commitSync(); // Коммитим оффсет после обработки всей пачки
                } catch (Exception e) {
                    LOGGER.error("Ошибка при коммите оффсета: {}", e.getMessage());
                }
            }
        }
    }
}