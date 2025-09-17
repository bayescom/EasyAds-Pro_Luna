package com.easyads.management.distribution.traffic.model;

import com.easyads.management.distribution.strategy.model.percentage.SdkPercentage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
@Data
public class SdkTraffic {
    @JsonIgnore
    private Integer id;
    private Integer expId;
    private String expName;
    private SdkPercentage trafficPercentage;
    private List<SdkTrafficGroup> trafficGroupList;
}
