package org.example.sinkfunction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

public class RedisSinkFunction<T> extends RichSinkFunction<T>  implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient JedisPool jedisPool;
    private final int redisDbIndex = 0; // 👉 你要选择的 Redis 逻辑库 index
    private final String host = "192.168.31.252";
    private final int port = 7001;
    private final int timeout = 2000;
    private final String password = "XIE2ZKZ6p1DZMpVq";

    // Key生成策略
    private final String keyPrefix ; //
    private final Function<T, String> keyFieldExtractor; // 从Entity提取Key字段的函数

    // 可选：设置TTL（秒）
    private final Integer ttlSeconds = 3600*12*15;


    public RedisSinkFunction(String keyPrefix,Function<T, String> keyFieldExtractor){
        this.keyPrefix = keyPrefix ;
        this.keyFieldExtractor = keyFieldExtractor ;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // 参数：主机，端口，超时，密码
        jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
    }
    @Override
    public void invoke(T entity, Context context) {
        try (Jedis jedis = jedisPool.getResource()){
            jedis.select(redisDbIndex);

            // 动态生成Redis Key
            String fieldValue = keyFieldExtractor.apply(entity);
            if (fieldValue == null) return;

            String redisKey = keyPrefix;

            String valueJson = JSON.toJSONString(entity);

            // 写入Hash
//            jedis.set(redisKey, valueJson);
            jedis.hset(redisKey,fieldValue,valueJson);
            // 设置TTL（可选）
            if (ttlSeconds != null) {
                jedis.expire(redisKey, ttlSeconds);
            }
        }

    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
