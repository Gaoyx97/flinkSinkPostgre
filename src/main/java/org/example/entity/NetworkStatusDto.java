package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class NetworkStatusDto {
    //节点名称
    private String sidName;
    //设备号
    private String deviceId;
    //速率
    private Integer transferRate;
    //SW信息
    private List<SWInformation> swInformation;
}
