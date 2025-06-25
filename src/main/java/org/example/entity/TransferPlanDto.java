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
public class TransferPlanDto {
    //跟踪接受计划号
    private String planId;
    //圈号
    private String circleNo;
    //任务标识
    private String taskCode;
    //设备标识
    private String deviceCode;
    //传输号
    private Integer transmissionNo;
    //工作方式
    private Integer workMode;
    //站节点
    private String sidName;
    //传输中心
    private String transmissionCenter;
    //优先级
    private Integer priorityLevel;
    //分配速率
    private Integer allocationRate;
    //中继卫星代号
    private String relaySatellite;
    //通道号/链路表示
    private String channelNo;
    //接收协议
    private Integer rxProtocol;
    //发送协议
    private Integer txProtocal;
    //接收帧数量
    private Integer rxFrameCount;
    //接收速率
    private Integer rxRate;
    //接收数据量
    private Integer rxDataVolume;
    //发送帧数量
    private Integer txFrameCount;
    //发送速率
    private Integer txRate;
    //发送数据量
    private Integer txDataVolume;
    //传输状态
    private Integer status;
    //当前传输节点：1-收到申请，2-向应用中心发送申请，3-收到应用中心申请回执，4-向设备发送申请回执，5-接收数据，6-发送数据，7-收到完成报告，8-想设备发送完成报告，9-向应用中心发送完成报告，10-收到应用中心报告回执。
    private List<Integer> currentNode;
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
}
