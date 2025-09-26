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
public class SdkStrategyPercentage {
    @JsonIgnore
    private static final Cloner cloner = new Cloner();

    private Integer strategy_percentage_exp_id;
    private String strategy_percentage_exp_name;
    private int strategy_percentage_id;
    private String strategy_percentage_name;
    private int percentage;
    private List<Sdk> suppliers;

    public SdkStrategyPercentage(SdkGroupStrategyOrigin sgso, Map<String, Sdk> sdkConfMap) {
        this.strategy_percentage_exp_id = sgso.getStrategy_percentage_exp_id();
        this.strategy_percentage_exp_name = sgso.getStrategy_percentage_exp_name();
        this.strategy_percentage_id = sgso.getStrategy_percentage_id();
        this.strategy_percentage_name = sgso.getStrategy_percentage_name();
        this.percentage = sgso.getStrategy_percentage();
        setSuppliersInfo(sgso, sdkConfMap);
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
