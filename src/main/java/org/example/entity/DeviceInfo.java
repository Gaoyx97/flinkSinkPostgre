package org.example.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class DeviceInfo {
    //大小
    private String size;
    //使用率
    private Double useRate;
}
