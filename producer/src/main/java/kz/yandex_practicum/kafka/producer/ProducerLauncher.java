package kz.yandex_practicum.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * Точка входа приложения для публикации сообщений в Kafka.
 *
 * @author maenlest
 */
public class ProducerLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerLauncher.class);

    private static final String TOPIC_NAME;
    private static final Properties PROPERTIES;

    static {
        TOPIC_NAME = "project-1-topic"; // Название топика

        PROPERTIES = new Properties();
        PROPERTIES.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092"); // Адреса брокеров Kafka
        PROPERTIES.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Сериализатор ключа
        PROPERTIES.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName()); // Сериализатор значения
        PROPERTIES.put(ProducerConfig.ACKS_CONFIG, "all"); // Ждем подтверждения от всех реплик
        PROPERTIES.put(ProducerConfig.RETRIES_CONFIG, 3); // Количество попыток отправки сообщения в случае ошибки
    }

    public static void main(String[] args) {
        try (Producer<String, Message> producer = new KafkaProducer<>(PROPERTIES)) {
            startPublishingMessages(producer);
        }
    }

    /**
     * Запускает процесс публикации сообщений в Kafka.
     *
     * @param producer настроенный продюсер для публикации сообщений.
     */
    private static void startPublishingMessages(Producer<String, Message> producer) {
        while (true) {
            try {
                Thread.sleep((long) (Math.random() * 900) + 100); // Ждем от 100 мс до 1000 мс перед публикацией следующего сообщения
            } catch (InterruptedException e) {
                LOGGER.error("Ошибка при публикации сообщения (InterruptedException)", e);
                continue; // Прерываем цикл, если поток был прерван
            }

            var value = new Message();

            LOGGER.info("Публикуем сообщение: {}", value);

            ProducerRecord<String, Message> record = new ProducerRecord<>(
                    TOPIC_NAME, UUID.randomUUID().toString(), value);

            try {
                producer.send(record);
            } catch (Exception e) {
                LOGGER.error("Ошибка при публикации сообщения", e);
            }
        }
    }
}