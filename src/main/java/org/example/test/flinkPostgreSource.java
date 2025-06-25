package org.example.test;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.source.PostgresSourceFunction;

import java.util.Map;

public class flinkPostgreSource {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        DataStream<Map<String, String>> mysqlSource = env
                .addSource(new PostgresSourceFunction()) // 你需要自定义这个 SourceFunction
                .setParallelism(1);

        mysqlSource.print();

        env.execute("Flink PostgreSQL");

    }
}
