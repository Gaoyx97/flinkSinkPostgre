package org.example.map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.example.entity.DeviceStatus;
import org.example.entity.NetworkStatus;
import org.example.entity.SoftwareStatus;
import org.example.util.MathUtil;

public class SoftwareStatusMap implements MapFunction<String, SoftwareStatus> {


    @Override
    public SoftwareStatus map(String jsonStr) throws Exception {

        SoftwareStatus softwareStatus =  JSON.parseObject(jsonStr, SoftwareStatus.class);

        if(!softwareStatus.problem().isEmpty()){
            softwareStatus.result("待处理");
        }else{
            softwareStatus.result("");
        }

        return softwareStatus;
    }

}
