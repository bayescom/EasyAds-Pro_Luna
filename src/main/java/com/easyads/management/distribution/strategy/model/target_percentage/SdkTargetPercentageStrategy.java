package com.easyads.management.distribution.strategy.model.target_percentage;

import com.easyads.component.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

public class SdkTargetPercentageStrategy {
    private Long trafficId;
    private SdkTargetPercentage targetPercentage;
    private Map<String, List<List>> sdkSuppliers;
    private List<List> suppliers;
    private String supplier_ids;

    public Long getTrafficId() {
        return trafficId;
    }

    public void setTrafficId(Long trafficId) {
        this.trafficId = trafficId;
    }

    public SdkTargetPercentage getTargetPercentage() {
        return targetPercentage;
    }

    public void setTargetPercentage(SdkTargetPercentage targetPercentage) {
        this.targetPercentage = targetPercentage;
    }

    @JsonIgnore
    public String getSupplier_ids() {
        return supplier_ids;
    }

    public void setSupplier_ids(String supplier_ids) {
        this.supplier_ids = supplier_ids;
    }

    public List<List> getSuppliers() {
        this.suppliers = JsonUtils.convertJsonToList(supplier_ids, List.class);
        return this.suppliers;
    }

    public Map<String, List<List>> getSdkSuppliers() {
        return sdkSuppliers;
    }

    public void setSdkSuppliers(Map<String, List<List>> sdkSuppliers) {
        this.sdkSuppliers = sdkSuppliers;
    }
}
