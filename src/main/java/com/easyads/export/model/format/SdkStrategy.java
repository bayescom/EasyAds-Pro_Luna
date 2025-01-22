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
    private List<Sdk> suppliers;

    public SdkStrategy(SdkGroupStrategyOrigin sgso, Map<String, Sdk> sdkConfMap) {
        this.strategy_id = sgso.getStrategy_id();
        this.strategy_name = sgso.getStrategy_name();
        this.priority = sgso.getPriority();
        this.target_info = new SdkStrategyTarget(sgso);
        setSuppliersInfo(sgso, sdkConfMap);
    }

    @Override
    public int compareTo(SdkStrategy ss) {
        return Integer.compare(this.getPriority(), ss.getPriority());
    }

    private void setSuppliersInfo(SdkGroupStrategyOrigin sgso, Map<String, Sdk> sdkConfMap) {
        this.suppliers = new ArrayList<>();
        Map<Integer, Pair<Integer, Integer>> supplierPriorityMap = sgso.getSupplierPriorityMap();
        for(Map.Entry<Integer, Pair<Integer,Integer>> entry : supplierPriorityMap.entrySet()) {
            int supplier_id = entry.getKey();
            Pair<Integer, Integer> priorityPair = entry.getValue();
            int priority = priorityPair.getKey();
            int index = priorityPair.getValue();
            Sdk sdk = sdkConfMap.get(String.valueOf(supplier_id));
            if(null != sdk) {
                Sdk sdkCopy = cloner.deepClone(sdk);
                String supplier_key = sgso.getAdspot_id() + "_" + supplier_id;
                sdkCopy.setSupplier_key(Base64.getEncoder().encodeToString(supplier_key.getBytes()));
                sdkCopy.setPriority(String.valueOf(priority));
                sdkCopy.setIndex(String.valueOf(index));
                this.suppliers.add(sdkCopy);
            }
        }
    }
}
