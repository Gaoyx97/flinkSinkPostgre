package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
public class NetworkLink {
    //节点名称
    private String sidName;
    //战网中心 (0 正常 , 1 不正常)
    Map<String,Integer> linkMap;
}
