package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class NetworkSource {
    //节点名称
    private String sidName;
    //拥塞率
    private Double congestionRate;
    //带宽使用率
    private Double bandwidthUseRate;
    //网络延时率
    private Double networkDelayRate;
    //丢包率
    private Double packLoseRate;

}
