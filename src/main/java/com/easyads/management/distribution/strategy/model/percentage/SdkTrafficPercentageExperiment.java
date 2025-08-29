package com.easyads.management.distribution.strategy.model.percentage;

import com.easyads.management.distribution.strategy.model.exp.SdkExperiment;
import lombok.Data;

import java.util.List;

@Data
// TODO AB测试 改名，包括成员变量
public class SdkTrafficPercentageExperiment {
    private SdkExperiment experiment;
    List<SdkPercentage> trafficPercentageList;
}
