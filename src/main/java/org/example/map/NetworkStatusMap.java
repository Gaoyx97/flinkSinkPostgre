package org.example.map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.example.entity.*;
import org.example.util.MathUtil;

import java.util.List;

public class NetworkStatusMap implements MapFunction<String, NetworkStatus> {

    /** 总带宽 **/
    private Integer bandwithCount =  100000;

    @Override
    public NetworkStatus map(String jsonStr) throws Exception {

        Integer portInputSum = 0;
        Integer portOutputSum = 0;


        NetworkStatusDto networkStatusDto = JSON.parseObject(jsonStr, NetworkStatusDto.class);

        List<SWInformation> swInformations = networkStatusDto.swInformation();

        for (SWInformation swInformation : swInformations) {
            portInputSum += swInformation.portInput();
            portOutputSum += swInformation.portOutput();

        }



        Double bandwidthUseRate = MathUtil.calculatePercentage( (portInputSum + portOutputSum), bandwithCount );
        String networkState = getNetworkState(bandwidthUseRate);

        NetworkStatus networkStatus = new NetworkStatus()
                .sidName(networkStatusDto.sidName())
                .deviceId(networkStatusDto.deviceId())
                .transferRate(networkStatusDto.transferRate())
                .bandwidthUseRate(bandwidthUseRate)
                .networkState(networkState)
                .result("");

        if(!networkState.equals("正常")){
            networkStatus.result("待处理");
        }

        return networkStatus;
    }

    public String getNetworkState(Double bandwidthUseRate){
        String networkState = "正常";
        if(bandwidthUseRate == 0){
            networkState = "断网";
        }else if(bandwidthUseRate > 60 && bandwidthUseRate <= 80){
            networkState = "高使用率";
        }else if(bandwidthUseRate > 80){
            networkState = "低带宽";
        }
        return networkState;
    }
}
