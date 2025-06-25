package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class SoftwareStatus {
    //节点名称
    private String sidName;
    //分机编号
    private String extensionCode;
    //应用软件
    private String application;
    //问题
    private String problem;
    //处理结果
    private String result;
}
