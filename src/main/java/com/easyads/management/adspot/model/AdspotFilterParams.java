package com.easyads.management.adspot.model;

import com.easyads.component.utils.DataStringUtils;

import java.util.List;
import java.util.Map;

public class AdspotFilterParams {
    private Map<String, Object> queryParams;

    // 筛选参数及翻页参数
    public List<Integer> mediaIds;
    public List<Integer> adspotTypes;
    public String searchText;
    public Integer status;
    public Integer offset, limit, page;

    public AdspotFilterParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        this.mediaIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("mediaIds", null), ',');
        this.searchText = (String) queryParams.getOrDefault("searchText", null);
        this.adspotTypes = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotTypes", null), ',');
        this.status = queryParams.containsKey("status") ? Integer.valueOf((String) queryParams.get("status")) : null;
        setLimit();
    }

    private void setLimit() {
        limit = queryParams.containsKey("limit") ? Integer.parseInt((String) queryParams.get("limit")) : null;
        page = queryParams.containsKey("page") ? Integer.parseInt((String) queryParams.get("page")) : null;
        if (limit != null && this.page != null) {
            offset = (page - 1) * limit;
        }
    }
}
