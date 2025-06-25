package org.example.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class testAmbari {
    private static final String AMBARI_URL = "http://bigdata:8080/api/v1/clusters/hdp/configurations/service_config_versions?service_name.in(YARN)&is_current=true&fields=*";
    private static final String USERNAME = "admin"; // 你的 Ambari 用户名
    private static final String PASSWORD = "admin"; // 你的 Ambari 密码

    public static void main(String[] args) {
        try {
            // 创建 URL 对象
            URL url = new URL(AMBARI_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置 HTTP 请求方法
            conn.setRequestMethod("GET");

            // 设置请求头
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:136.0) Gecko/20100101 Firefox/136.0");
            conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("X-Requested-By", "X-Requested-By");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Referer", "http://bigdata:8080/");
            conn.setRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("Cache-Control", "no-cache");

            // 设置认证信息（Basic Authentication）
            String auth = USERNAME + ":" + PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // 获取响应码
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 获取响应的输入流，支持解压缩
                InputStream inputStream = conn.getInputStream();
                String encoding = conn.getHeaderField("Content-Encoding");
                if ("gzip".equalsIgnoreCase(encoding)) {
                    inputStream = new GZIPInputStream(inputStream);
                }

                // 读取响应内容并指定 UTF-8 编码
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 输出响应内容
                System.out.println("Response: " + response.toString());
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
