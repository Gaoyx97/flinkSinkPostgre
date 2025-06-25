package org.example.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.example.entity.NetworkLink;
import org.example.entity.SoftwareStatus;
import org.example.map.NetworkLinkMap;
import org.example.map.SoftwareStatusMap;
import org.example.sinkfunction.RedisSinkFunction;
import org.example.source.PostgresSourceFunction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * 网络链路
 */

public class flinkNetworkLink {

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
        properties.setProperty("group.id", "flink-kafka-network-link");

        // 3. 创建Kafka Source
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "network_link",                             // 源 topic
                new SimpleStringSchema(),                  // 简单字符串序列化w
                properties);

//        consumer.setStartFromLatest();

        consumer.setStartFromGroupOffsets();
        // 4. 消费kafka 数据
        DataStreamSource<String> kafkaStream = env.addSource(consumer);

//        kafkaStream.print();
        // 5. 进行数据清洗
        SingleOutputStreamOperator<NetworkLink> parsedStream = kafkaStream
                .map(new NetworkLinkMap())
                .name("Parse with Fastjson");

        MapStateDescriptor<String, String> broadcastStateDescriptor =
                new MapStateDescriptor<>("sid_dictionaries", Types.STRING, Types.STRING);

        DataStream<Map<String, String>> postgresSource = env
                .addSource(new PostgresSourceFunction()) // 你需要自定义这个 SourceFunction
                .setParallelism(1);
//        postgresSource.print();

        BroadcastStream<Map<String, String>> broadcastStream =
                postgresSource.broadcast(broadcastStateDescriptor);

        DataStream<NetworkLink> processed = parsedStream
                .connect(broadcastStream)
                .process(new BroadcastProcessFunction<NetworkLink, Map<String, String> ,NetworkLink>() {

                    @Override
                    public void processElement(NetworkLink networkLink, BroadcastProcessFunction<NetworkLink, Map<String, String>, NetworkLink>.ReadOnlyContext ctx, Collector<NetworkLink> out) throws Exception {
                        ReadOnlyBroadcastState<String, String> broadcastState =
                                ctx.getBroadcastState(broadcastStateDescriptor);
                        String sidName = broadcastState.get(networkLink.sidName());
                        if(!sidName.isEmpty()){
                            networkLink.sidName(sidName);
                        }

                        out.collect(networkLink);

                    }

                    @Override
                    public void processBroadcastElement(Map<String, String> stringStringMap, BroadcastProcessFunction<NetworkLink, Map<String, String>, NetworkLink>.Context ctx, Collector<NetworkLink> out) throws Exception {
                        BroadcastState<String, String> state = ctx.getBroadcastState(broadcastStateDescriptor);

                        state.clear(); // 清空旧的广播数据（可选）
                        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                            state.put(entry.getKey(), entry.getValue());
                        }
                    }
                });

        processed.print();
        // 9. 存入 redis
        processed.addSink(
                new RedisSinkFunction("network_link", new NetworkLinkKeyExtractor()))
                .name("Redis Sink");


        // 10. 启动作业
        env.execute("Flink Kafka Source to Sink Job");
    }


    public static class NetworkLinkKeyExtractor implements Function<NetworkLink, String>, Serializable {

        private static final long serialVersionUID = 1L;
        @Override
        public String apply(NetworkLink source) {
            return source.sidName();
        }
    }

}
