package com.easyads.management.experiment.exp.model.filter;


import com.easyads.component.utils.DataStringUtils;

import java.util.List;
import java.util.Map;

public class SdkExperimentFilterParams {
    private Map<String, Object> queryParams;

    // 筛选参数和翻页参数
    public Long companyId;
    public List<Integer> mediaIds;
    public List<Integer> adspotIds;
    public List<Integer> expIds;
    public Integer expType;
    public Integer status;
    public Integer offset, limit, page;


    public SdkExperimentFilterParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        this.mediaIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("mediaIds", null), ',');
        this.adspotIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotIds", null), ',');
        this.expIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("expIds", null), ',');
        this.expType = queryParams.containsKey("expType") ? Integer.parseInt((String) queryParams.get("expType")) : null;
        this.status = queryParams.containsKey("status") ? Integer.parseInt((String) queryParams.get("status")) : null;
        setLimit();
    }

    private void setLimit() {
        limit = queryParams.containsKey("limit") ? Integer.parseInt((String) queryParams.get("limit")) : null;
        page = queryParams.containsKey("page") ? Integer.parseInt((String) queryParams.get("page")) : null;
        if (limit != null && this.page != null) {
            offset = (page - 1) * limit;
        }
    }

    public boolean isInvalid() {
        return null == this.companyId;
    }
}
