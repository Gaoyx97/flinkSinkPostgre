package org.example.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class DeviceStatus {
    //站节点
    private String sidName;
    //设备名称
    private String deviceName;
    //设备类型
    private String deviceType;
    //CPU
    private DeviceInfo cpuInfo;
    //内存
    private DeviceInfo memory;
    //磁盘
    private DeviceInfo disk;
}
