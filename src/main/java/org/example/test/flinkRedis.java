package org.example.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.example.sinkfunction.PostgresSinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class flinkRedis {
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

        consumer.setStartFromLatest();

        // 4. 消费kafka 数据
        DataStreamSource<String> kafkaStream = env.addSource(consumer);

        // 5. 进行数据清洗
        SingleOutputStreamOperator<JSONObject> parsedStream = kafkaStream
                .map(new MapFunction<String, JSONObject>() {
                    @Override
                    public JSONObject map(String s) throws Exception {
                        JSONObject json = JSONObject.parseObject(s);
                        json.put("username","test"+json.getString("id"));
                        return json;
                    }
                })
                .name("Parse with Fastjson");


        final OutputTag<JSONObject> postgresTag = new OutputTag<JSONObject>("postgres-side-output") {};

        // 6. 分流处理函数
        SingleOutputStreamOperator<JSONObject> mainStream = parsedStream.process(
                new ProcessFunction<JSONObject, JSONObject>() {
                    @Override
                    public void processElement(JSONObject json, Context ctx, Collector<JSONObject> out) throws Exception {
                        // 所有数据都保留在主流
                        out.collect(json);

                        // 分流逻辑：如果 type 字段等于 "target"，就发到副流
                        if (15 == (json.getIntValue("id"))) {
                            ctx.output(postgresTag, json);
                        }
                    }
                }
        );

        // 7. 获取副流
        DataStream<JSONObject> postgresStream = mainStream.getSideOutput(postgresTag);

        // 8. 存入postgres
        postgresStream.addSink(
                PostgresSinkFunction.createPostgresSink(
                        "INSERT INTO testsql (id,age, username) VALUES (?, ?, ?)",
                        new JdbcStatementBuilder<JSONObject>() {
                            @Override
                            public void accept(PreparedStatement ps, JSONObject json) throws SQLException {
                                ps.setInt(1, json.getIntValue("id")); // 设置 ID
                                ps.setInt(2, json.getIntValue("age")); // 设置 AGE
                                ps.setString(3, json.getString("username"));
                            }
                        }
                )
        );

        // 9. 存入 redis
//        parsedStream.addSink(new RedisSinkFunction()).name("Redis Sink");


        // 10. 启动作业
        env.execute("Flink Kafka Source to Sink Job");
    }


}
