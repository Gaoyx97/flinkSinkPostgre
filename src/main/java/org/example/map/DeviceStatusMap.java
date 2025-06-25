package org.example.map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.example.entity.DeviceStatus;
import org.example.entity.NetworkLink;

public class DeviceStatusMap implements MapFunction<String, DeviceStatus> {


    @Override
    public DeviceStatus map(String jsonStr) throws Exception {

        DeviceStatus deviceStatus = JSON.parseObject(jsonStr,DeviceStatus.class);

        return deviceStatus;
    }

}
