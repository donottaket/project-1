package kz.yandex_practicum.kafka.single_message_consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Точка входа приложения для получения одного сообщения из Kafka.
 *
 * @author maenlest
 */
public class SingleMessageConsumerLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleMessageConsumerLauncher.class);

    private static final String TOPIC_NAME;
    private static final Properties PROPERTIES;

    static {
        TOPIC_NAME = "project-1-topic"; // Название топика

        PROPERTIES = new Properties();
        PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092"); // Адреса брокеров Kafka
        PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Десериализатор ключа
        PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName()); // Десериализатор значения
        PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "single-group"); // Идентификатор группы потребителей
        PROPERTIES.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // Авто-коммит включен
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
    private static void startConsumingMessages(Consumer<String, Message> consumer) {
        consumer.subscribe(List.of(TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, Message> records;

            try {
                records = consumer.poll(Duration.ofMillis(100L)); // Получаем сообщения из топика
            } catch (Exception e) {
                LOGGER.error("Ошибка при получении сообщений: {}", e.getMessage());
                continue; // Пропускаем итерацию, если произошла ошибка
            }

            if (!records.isEmpty() && records.count() > 0) {
                LOGGER.info("Получено {} сообщений", records.count());

                for (ConsumerRecord<String, Message> record : records) {
                    LOGGER.info("Получено сообщение: {}", record.value());
                }
            }
        }
    }
}