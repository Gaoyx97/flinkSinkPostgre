package org.example.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class flinkPostgre {
    public static void main(String[] args) throws Exception {

        // 1. 设置执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. Kafka连接配置
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.31.231:9092"); // 替换为你的Kafka地址
        properties.setProperty("group.id", "flink-kafka-group");

        // 3. 创建Kafka Source
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "test",                             // 源 topic
                new SimpleStringSchema(),                  // 简单字符串序列化
                properties);

        // 从 Kafka 中读取数据流
        DataStream<String> stream = env.addSource(consumer);

        // 假设 Kafka 消息是 JSON 格式的
        DataStream<JSONObject> processedStream = stream
                .map(new MapFunction<String, JSONObject>() {
                    @Override
                    public JSONObject map(String value) throws Exception {
                        // 假设 JSON 数据格式为 {"id": 1, "name": "Alice"}
                        JSONObject json = JSONObject.parseObject(value);
                        json.put("username","test"+json.getString("id"));
                        return json;
                    }
                });

        // 将数据写入 PostgreSQL
        processedStream.addSink(JdbcSink.sink(
                "INSERT INTO testsql (id,age, username) VALUES (?, ?, ?)", // SQL 插入语句
                new JdbcStatementBuilder<JSONObject>() {
                    @Override
                    public void accept(PreparedStatement ps, JSONObject record) throws SQLException {
                        ps.setInt(1, record.getIntValue("id")); // 设置 ID
                        ps.setInt(2, record.getIntValue("age")); // 设置 AGE
                        ps.setString(3, record.getString("username"));
                    }
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(10) // 批量大小
                        .withBatchIntervalMs(2000) // 批次时间间隔
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withDriverName("org.postgresql.Driver")
                        .withUrl("jdbc:postgresql://192.168.31.231:5432/mydb") // PostgreSQL URL
                        .withUsername("postgres") // PostgreSQL 用户名
                        .withPassword("Pg$3Yx8@uR7fL2!vNcWz") // PostgreSQL 密码
                        .build()
        ));

        // 执行作业
        env.execute("Flink Kafka to PostgreSQL");
    }

}
