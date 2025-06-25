package org.example.sinkfunction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.example.entity.TransferPlan;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

public class PlanReduceRedisSinkFunction extends RichSinkFunction<TransferPlan>  implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient JedisPool jedisPool;
    private final int redisDbIndex = 0; // 👉 你要选择的 Redis 逻辑库 index
    private final String host = "192.168.31.252";
    private final int port = 7001;
    private final int timeout = 2000;
    private final String password = "XIE2ZKZ6p1DZMpVq";

    // Key生成策略
    private final String keyPrefix ; //
    // 从Entity提取Key字段的函数

    // 可选：设置TTL（秒）
    private final Integer ttlSeconds = 3600*12*15;


    public PlanReduceRedisSinkFunction(String keyPrefix){
        this.keyPrefix = keyPrefix ;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // 参数：主机，端口，超时，密码
        jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
    }
    @Override
    public void invoke(TransferPlan plan, Context context) {
        try (Jedis jedis = jedisPool.getResource()){
            jedis.select(redisDbIndex);

            // 动态生成Redis Key
            String fieldValue = plan.planId()+"_"+plan.circleNo()+"_"+plan.taskCode()+"_"+plan.deviceCode();
            if (fieldValue == null) return;

            String redisKey = keyPrefix;

            if(jedis.hexists(redisKey,fieldValue)){
                String existingJson = jedis.hget(redisKey, fieldValue);
                JSONObject mergedJson = JSON.parseObject(existingJson);

                mergedJson.put("rxFrameCount", mergedJson.getInteger("rxFrameCount") + plan.rxFrameCount());
                mergedJson.put("rxRate", mergedJson.getInteger("rxRate") + plan.rxRate());
                mergedJson.put("rxDataVolume", mergedJson.getInteger("rxDataVolume") + plan.rxDataVolume());
                mergedJson.put("txFrameCount", mergedJson.getInteger("txFrameCount") + plan.txFrameCount());
                mergedJson.put("txRate", mergedJson.getInteger("txRate") + plan.txRate());
                mergedJson.put("txDataVolume", mergedJson.getInteger("txDataVolume") + plan.txDataVolume());

                jedis.hset(redisKey,fieldValue,mergedJson.toJSONString());

            }else {
                String valueJson = JSON.toJSONString(plan);
                jedis.hset(redisKey,fieldValue,valueJson);
            }

            // 写入Hash
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
