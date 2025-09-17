package com.easyads.management.distribution.traffic.model;

import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentageStrategy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class SdkTrafficGroup {
    @JsonIgnore
    private Long groupTargetId; // 不加这个没办法让mybatis聚合
    private SdkGroupStrategy groupStrategy;
    private Integer expId;
    private String expName;
    private List<SdkTargetPercentageStrategy> targetPercentageStrategyList;
}
