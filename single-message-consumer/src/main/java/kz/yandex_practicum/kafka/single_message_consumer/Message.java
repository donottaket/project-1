package kz.yandex_practicum.kafka.single_message_consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Структура сообщения.
 *
 * @author maenlest
 */
public class Message {
    private final String field1;
    private final double field2;
    private final boolean field3;
    private final List<String> field4 = new ArrayList<>();

    public Message() {
        this.field1 = UUID.randomUUID().toString();
        this.field2 = Math.random();
        this.field3 = Math.random() > 0.5;

        for (var i = 0; i < 10; ++i) {
            this.field4.add(UUID.randomUUID().toString());
        }
    }

    public String getField1() {
        return field1;
    }

    public double getField2() {
        return field2;
    }

    public boolean isField3() {
        return field3;
    }

    public List<String> getField4() {
        return field4;
    }

    @Override
    public String toString() {
        return "Message{" +
                "field1='" + field1 + '\'' +
                ", field2=" + field2 +
                ", field3=" + field3 +
                ", field4=" + field4 +
                '}';
    }
}