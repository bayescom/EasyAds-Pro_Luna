package com.easyads.management.distribution.traffic.model;

import com.easyads.management.distribution.strategy.model.percentage.SdkPercentageStrategy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
@Data
public class SdkTraffic {
    @JsonIgnore
    private Integer id;
    private SdkPercentageStrategy trafficPercentage;
    private List<SdkTrafficGroup> trafficGroupList;
}
