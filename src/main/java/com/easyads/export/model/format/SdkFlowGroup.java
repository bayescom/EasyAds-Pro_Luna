package com.easyads.export.model.format;


import com.easyads.export.model.origin.SdkGroupStrategyOrigin;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class SdkFlowGroup {
    private Integer group_percentage_exp_id;
    private String group_percentage_exp_name;
    private int group_id;
    private String group_name;
    private int percentage;
    private List<SdkStrategy> strategy;

    public SdkFlowGroup(List<SdkGroupStrategyOrigin> sdkGroupStrategyOriginList,
                        Map<String, Sdk> sdkConfMap) {
        SdkGroupStrategyOrigin sgso = sdkGroupStrategyOriginList.get(0);
        this.group_id = sgso.getGroup_id();
        this.group_percentage_exp_id = sgso.getGroup_exp_id();
        this.group_percentage_exp_name = sgso.getGroup_exp_name();
        this.group_name = sgso.getGroup_name();
        this.percentage = sgso.getPercentage();
        setSdkStrategy(sdkGroupStrategyOriginList, sdkConfMap);
    }

    private void setSdkStrategy(List<SdkGroupStrategyOrigin> sdkGroupStrategyOriginList,
                                Map<String, Sdk> sdkConfMap) {
        this.strategy = new ArrayList<>();
        for(SdkGroupStrategyOrigin sgso : sdkGroupStrategyOriginList) {
            SdkStrategy sdkStrategy = new SdkStrategy(sgso, sdkConfMap);
            this.strategy.add(sdkStrategy);
        }
        Collections.sort(this.strategy);
    }
}
