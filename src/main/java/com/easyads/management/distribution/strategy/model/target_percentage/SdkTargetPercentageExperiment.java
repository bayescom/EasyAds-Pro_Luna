package com.easyads.management.distribution.strategy.model.target_percentage;

import com.easyads.management.distribution.strategy.model.exp.SdkExperiment;
import lombok.Data;

import java.util.List;

@Data
// TODO AB测试 改名，包括成员变量
public class SdkTargetPercentageExperiment {
    private SdkExperiment experiment;
    List<SdkTargetPercentage> targetPercentageList;
}
