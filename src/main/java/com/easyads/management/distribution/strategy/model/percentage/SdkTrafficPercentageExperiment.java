package com.easyads.management.distribution.strategy.model.percentage;

import com.easyads.management.distribution.strategy.model.exp.SdkExperiment;
import lombok.Data;

import java.util.List;

@Data
public class SdkTrafficPercentageExperiment {
    private SdkExperiment experiment;
    private List<SdkPercentage> trafficPercentageList;
}
