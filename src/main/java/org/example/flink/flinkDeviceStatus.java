package org.example.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.example.entity.DeviceStatus;
import org.example.entity.SoftwareStatus;
import org.example.map.DeviceStatusMap;
import org.example.map.SoftwareStatusMap;
import org.example.sinkfunction.RedisSinkFunction;

import java.io.Serializable;
import java.util.Properties;
import java.util.function.Function;

/**
 * 设备状态
 */

public class flinkDeviceStatus {

    public static void main(String[] args) throws Exception {

        // 1. 设置执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(1000); // 每 1 秒保存一次状态
        env.getCheckpointConfig().setCheckpointStorage("file:///D:/project/checkpoint");
        // 可选的更详细配置
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.31.215:9092");
        properties.setProperty("group.id", "flink-kafka-device-status");

        // 3. 创建Kafka Source
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "device_status",                             // 源 topic
                new SimpleStringSchema(),                  // 简单字符串序列化w
                properties);

//        consumer.setStartFromLatest();

        consumer.setStartFromGroupOffsets();
        // 4. 消费kafka 数据
        DataStreamSource<String> kafkaStream = env.addSource(consumer);

//        kafkaStream.print();
        // 5. 进行数据清洗
        SingleOutputStreamOperator<DeviceStatus> parsedStream = kafkaStream
                .map(new DeviceStatusMap())
                .name("Parse with Fastjson");

        parsedStream.print();
        // 9. 存入 redis
        parsedStream.addSink(
                new RedisSinkFunction("device_status", new DeviceStatusKeyExtractor()))
                .name("Redis Sink");


        // 10. 启动作业
        env.execute("Flink Kafka Source to Sink Job");
    }


    public static class DeviceStatusKeyExtractor implements Function<DeviceStatus, String>, Serializable {

        private static final long serialVersionUID = 1L;
        @Override
        public String apply(DeviceStatus source) {
            return source.deviceName();
        }
    }

}
