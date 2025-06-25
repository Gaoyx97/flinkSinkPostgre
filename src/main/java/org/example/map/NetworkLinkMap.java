package org.example.map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.table.planner.expressions.In;
import org.example.entity.DeviceStatus;
import org.example.entity.NetworkLink;
import org.example.entity.SoftwareStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkLinkMap implements MapFunction<String, NetworkLink> {


    @Override
    public NetworkLink map(String jsonStr) throws Exception {

//        NetworkLink networkLink = JSON.parseObject(jsonStr,NetworkLink.class);

        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String sidName = jsonObject.getString("sidName");
        jsonObject.remove("sidName");
        Map<String, Integer> linkMap = new HashMap<>();
        for (String linkKey : jsonObject.keySet()) {
            linkMap.put(linkKey,jsonObject.getInteger(linkKey));
        }

        NetworkLink networkLink = new NetworkLink()
                .sidName(sidName)
                .linkMap(linkMap);

        return networkLink;
    }

}
