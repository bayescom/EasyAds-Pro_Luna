package com.easyads.management.distribution.strategy.model.direction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrafficTarget {
    private int dimensionId;
    private String property;
    private List<Integer> valueIdList; // 自定义定向ID信息
    private List<String> valueDetailList; // 自定义定向带GroupId的详细信息
}
