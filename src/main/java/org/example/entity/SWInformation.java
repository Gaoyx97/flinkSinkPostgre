package org.example.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class SWInformation {
    //当前延时
    private Integer nowNetworkDelay;
    //入口流量
    private Integer portInput;
    //出口流量
    private Integer portOutput;
    //入口丢包
    private Integer portInPackLose;
    //出口丢包
    private Integer portOutPackLose;
}
