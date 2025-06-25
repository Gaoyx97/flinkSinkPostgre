package org.example.sinkfunction;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class PostgresSinkFunction  {

    private transient Connection connection;
    private PreparedStatement ps;
    private static String url = "jdbc:postgresql://192.168.31.231:5432/mydb";
    private static String user = "postgres";
    private static String password = "Pg$3Yx8@uR7fL2!vNcWz";

    public static <T> SinkFunction<T> createPostgresSink(
            String sql,
            JdbcStatementBuilder<T> builder
    ) {
        return JdbcSink.sink(
                sql,
                builder,
                JdbcExecutionOptions.builder()
                        .withBatchSize(100)
                        .withBatchIntervalMs(2000)
                        .withMaxRetries(3)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(url)
                        .withDriverName("org.postgresql.Driver")
                        .withUsername(user)
                        .withPassword(password)
                        .build()
        );
    }
}
