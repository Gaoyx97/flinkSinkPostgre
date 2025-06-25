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
public class NetworkSourceDto {
    //节点名称
    private String sidName;
    //SW信息
    private List<SWInformation> swInformation;
}
