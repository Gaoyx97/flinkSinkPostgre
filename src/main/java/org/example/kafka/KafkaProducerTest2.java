package org.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaProducerTest2 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.31.215:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 2. 创建生产者
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // 3. 要发送的消息列表
            //网络资源统计
            String messages = "{\"sidName\":\"0X10001005\",\"swInformation\":[{\"portInput\":30203,\"portOutput\":10210,\"nowNetworkDelay\":260,\"portInPackLose\":237,\"portOutPackLose\":225},{\"portInput\":20430,\"portOutput\":10202,\"nowNetworkDelay\":287,\"portInPackLose\":219,\"portOutPackLose\":148}]}";
            producer.send(new ProducerRecord<>("network_source", messages));
            // 网络状态统计
//            String messages = "{\"deviceId\":\"WX000\",\"sidName\":\"0X10001000\",\"transferRate\":87687,\"swInformation\":[{\"portInput\":11322,\"portOutput\":8000},{\"portInput\":11133,\"portOutput\":9956}]}";
//            producer.send(new ProducerRecord<>("network_status", messages));
            //软件异常
//            String messages = "{\"extensionCode\":\"920269\",\"sidName\":\"0X10001005\",\"application\":\"负载均衡\",\"problem\":\"响应延迟\"}";
//            producer.send(new ProducerRecord<>("software_status", messages));
            //网络链路
//            String messages = "{\"sidName\":\"0X10001005\",\"0X10002001\":0,\"0X10002002\":1,\"0X10002003\":0}";
//            producer.send(new ProducerRecord<>("network_link", messages));
            // 设备监控
//            String messages = "{\"deviceName\":\"服务器01\",\"cpuInfo\":{\"size\":\"100M\",\"useRate\":60.43},\"memory\":{\"size\":\"200M\",\"useRate\":30.21},\"disk\":{\"size\":\"1T\",\"useRate\":40.67}}";
//            producer.send(new ProducerRecord<>("device_status", messages));
            //数传任务
//            String messages = "{\"sidName\":\"0X10001004\",\"planId\":\"202505304\",\"circleNo\":\"05304\",\"channelNo\":\"0004\",\"priorityLevel\":3,\"taskCode\":\"Test04\",\"deviceCode\":\"TJTK44113\",\"transmissionCenter\":\"北京中心\",\"transmissionNo\":1,\"workMode\":1,\"allocationRate\":45000,\"relaySatellite\":\"WX002\",\"rxProtocol\":2,\"txProtocal\":2,\"rxFrameCount\":323,\"rxRate\":12500000,\"rxDataVolume\":46000,\"txFrameCount\":300,\"txRate\":12002413,\"txDataVolume\":40300,\"status\":2,\"currentNode\":[1,1,1,1,1,1,1,1,3,2],\"startTime\":\"20250507152200\",\"endTime\":\"20250507154000\"}";
//            producer.send(new ProducerRecord<>("transfer_plan", messages));
            // 5. 确保所有消息发送完成
            producer.flush();
            System.out.println("所有消息发送完毕！");
        }
    }
}
