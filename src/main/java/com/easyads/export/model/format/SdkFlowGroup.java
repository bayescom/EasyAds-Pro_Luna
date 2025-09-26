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

    public SdkFlowGroup(int group_id,
                        Map<Integer, List<SdkGroupStrategyOrigin>> sdkGroupStrategyOriginMap,
                        Map<String, Sdk> sdkConfMap) {
        SdkGroupStrategyOrigin sgso = sdkGroupStrategyOriginMap.values().iterator().next().get(0);
        this.group_id = group_id;
        this.group_percentage_exp_id = sgso.getGroup_exp_id();
        this.group_percentage_exp_name = sgso.getGroup_exp_name();
        this.group_name = sgso.getGroup_name();
        this.percentage = sgso.getPercentage();
        setSdkStrategy(sdkGroupStrategyOriginMap, sdkConfMap);
    }

    private void setSdkStrategy(Map<Integer, List<SdkGroupStrategyOrigin>> sdkGroupStrategyOriginMap,
                                Map<String, Sdk> sdkConfMap) {
        this.strategy = new ArrayList<>();
        for(Map.Entry<Integer, List<SdkGroupStrategyOrigin>> entry : sdkGroupStrategyOriginMap.entrySet()) {
            List<SdkGroupStrategyOrigin> sdkGroupStrategyOriginList = entry.getValue();
            int strategy_id = sdkGroupStrategyOriginList.get(0).getStrategy_id();
            SdkStrategy sdkStrategy = new SdkStrategy(strategy_id, sdkGroupStrategyOriginList, sdkConfMap);
            this.strategy.add(sdkStrategy);
        }
        Collections.sort(this.strategy);
    }
}
