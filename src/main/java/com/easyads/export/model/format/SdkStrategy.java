package com.easyads.export.model.format;


import com.easyads.export.model.origin.SdkGroupStrategyOrigin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rits.cloning.Cloner;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Getter
public class SdkStrategy implements Comparable<SdkStrategy> {
    @JsonIgnore
    private static final Cloner cloner = new Cloner();

    private int strategy_id;
    private String strategy_name;
    private int priority;
    private SdkStrategyTarget target_info;
    private List<SdkStrategyPercentage> strategyPercentageList;

    public SdkStrategy(int strategy_id,
                       List<SdkGroupStrategyOrigin> sdkGroupStrategyOriginList,
                       Map<String, Sdk> sdkConfMap) {
        this.strategy_id = strategy_id;
        SdkGroupStrategyOrigin sgso = sdkGroupStrategyOriginList.get(0);
        this.strategy_name = sgso.getStrategy_name();
        this.priority = sgso.getPriority();
        this.target_info = new SdkStrategyTarget(sgso);
        setStrategyPercentageListInfo(sdkGroupStrategyOriginList, sdkConfMap);
    }

    @Override
    public int compareTo(SdkStrategy ss) {
        return Integer.compare(this.getPriority(), ss.getPriority());
    }

    private void setStrategyPercentageListInfo(List<SdkGroupStrategyOrigin> sdkGroupStrategyOriginList,
                                               Map<String, Sdk> sdkConfMap) {
        this.strategyPercentageList = new ArrayList<>();
        for(SdkGroupStrategyOrigin sgso : sdkGroupStrategyOriginList) {
            SdkStrategyPercentage strategyPercentage = new SdkStrategyPercentage(sgso, sdkConfMap);
            this.strategyPercentageList.add(strategyPercentage);
        }
    }
}
