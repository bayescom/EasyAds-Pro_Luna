package com.easyads.management.distribution.traffic.model;

import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

public class SdkTrafficGroup {
    private Long trafficId;
    private SdkGroupStrategy groupStrategy;
    private Map<String, List<List>> sdkSuppliers;
    private List<List> suppliers;
    private String supplier_ids;

    public Long getTrafficId() {
        return trafficId;
    }

    public void setTrafficId(Long trafficId) {
        this.trafficId = trafficId;
    }

    public SdkGroupStrategy getGroupStrategy() {
        return groupStrategy;
    }

    public void setGroupStrategy(SdkGroupStrategy groupStrategy) {
        this.groupStrategy = groupStrategy;
    }

    @JsonIgnore
    public String getSupplier_ids() {
        return supplier_ids;
    }

    public void setSupplier_ids(String supplier_ids) {
        this.supplier_ids = supplier_ids;
    }

    public List<List> getSuppliers() {
        this.suppliers = JsonUtils.convertJsonToList(this.supplier_ids, List.class);
        return this.suppliers;
    }

    public Map<String, List<List>> getSdkSuppliers() {
        return sdkSuppliers;
    }

    public void setSdkSuppliers(Map<String, List<List>> sdkSuppliers) {
        this.sdkSuppliers = sdkSuppliers;
    }
}
