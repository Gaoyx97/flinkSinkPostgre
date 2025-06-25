package org.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaProducerTest {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.31.231:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 2. 创建生产者
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // 3. 要发送的消息列表
            List<String> messages = new ArrayList<>();
            String ids = "2";
            for (int i = 0; i < 10; i++) {
                String s = "{\"id\":"+(ids+i)+",\"age\":"+(ids+i)+"}";
                messages.add(s);
            }


            // 4. 批量发送
            for (String msg : messages) {
                producer.send(new ProducerRecord<>("test", msg));
                System.out.println("已发送: " + msg);
            }

            // 5. 确保所有消息发送完成
            producer.flush();
            System.out.println("所有消息发送完毕！");
        }
    }
}
