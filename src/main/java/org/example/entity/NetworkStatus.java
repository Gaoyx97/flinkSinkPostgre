package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class NetworkStatus {
    //节点名称
    private String sidName;
    //设备号
    private String deviceId;
    //带宽使用率
    private Double bandwidthUseRate;
    //速率
    private Integer transferRate;
    //网络状态
    private String networkState;
    //处理结果
    private String result;
}
