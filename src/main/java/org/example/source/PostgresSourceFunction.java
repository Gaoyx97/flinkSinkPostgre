package org.example.source;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PostgresSourceFunction extends RichSourceFunction<Map<String, String>> {
    private volatile boolean running = true;

    private static String url = "jdbc:postgresql://192.168.31.251:5444/op_management";
    private static String user = "postgres";
    private static String password = "esvtek!@#123";

    @Override
    public void run(SourceContext<Map<String, String>> ctx) throws Exception {
        Class.forName("org.postgresql.Driver");

        while (running) {
            Map<String, String> resultMap = new HashMap<>();

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conn.prepareStatement("SELECT sid_name, sid_code FROM public.sid_dictionaries");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    resultMap.put(rs.getString("sid_code"), rs.getString("sid_name"));
                }

                ctx.collect(resultMap);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Thread.sleep(60000); // 每分钟拉一次表
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}


