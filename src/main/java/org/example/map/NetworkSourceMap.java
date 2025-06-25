package org.example.map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.table.planner.expressions.In;
import org.apache.flink.util.Collector;
import org.example.entity.NetworkSource;
import org.example.entity.NetworkSourceDto;
import org.example.entity.NetworkStatusDto;
import org.example.entity.SWInformation;
import org.example.util.MathUtil;

import java.util.List;

public class NetworkSourceMap implements MapFunction<String, NetworkSource> {

    /** 最小延迟时长 **/
    private Integer minNetworkDelay = 100;
    /** 最大延迟时长 **/
    private Integer maxNetworkDelay = 500;
    /** 总带宽 **/
    private Integer bandwithCount =  100000;

    private Double W1 = 0.5;

    private Double W2 = 0.2;

    private Double W3 = 0.3;

    @Override
    public NetworkSource map(String jsonStr) throws Exception {

        Integer portInputSum = 0;
        Integer portOutputSum = 0;
        Integer nowNetworkDelayMax = Integer.MIN_VALUE;
        Integer portInPackLoseSum = 0;
        Integer portOutPackLoseSum = 0;

        NetworkSourceDto networkSourceDto = JSON.parseObject(jsonStr, NetworkSourceDto.class);

        List<SWInformation> swInformations = networkSourceDto.swInformation();

        for (SWInformation swInformation : swInformations) {
            //取出最大值
            int currentDelay = swInformation.nowNetworkDelay();
            nowNetworkDelayMax = Math.max(nowNetworkDelayMax, currentDelay);
            // 累加其他字段
            portInputSum += swInformation.portInput();
            portOutputSum += swInformation.portOutput();
            portInPackLoseSum += swInformation.portInPackLose();
            portOutPackLoseSum += swInformation.portOutPackLose();
        }

        Double bandwidthUseRate = MathUtil.calculatePercentage( (portInputSum + portOutputSum), bandwithCount );
        Double networkDelayRate = MathUtil.calculatePercentage( (nowNetworkDelayMax - minNetworkDelay ) , (maxNetworkDelay - minNetworkDelay ) );
        Double packLoseRate = MathUtil.calculatePercentage( (portInPackLoseSum + portOutPackLoseSum) , (portInputSum + portOutputSum) );
        Double congestionRate = MathUtil.reserveDecimal(W1*bandwidthUseRate + W2*networkDelayRate + W3*packLoseRate ,2 ) ;

        NetworkSource networkSource = new NetworkSource()
                .sidName(networkSourceDto.sidName())
                .congestionRate(congestionRate)
                .bandwidthUseRate(bandwidthUseRate)
                .networkDelayRate(networkDelayRate)
                .packLoseRate(packLoseRate);
        return networkSource;
    }
}
