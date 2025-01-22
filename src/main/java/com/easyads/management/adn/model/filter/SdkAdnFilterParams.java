package com.easyads.management.adn.model.filter;

import java.util.Map;

public class SdkAdnFilterParams {
    private Map<String, Object> queryParams;

    // 这些是请求参数
    public String searchText;
    public Integer status;
    public Integer dateType;
    public Long beginTime;
    public Long endTime;
    public Integer offset, limit, page;

    public SdkAdnFilterParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        this.status = queryParams.containsKey("status") ? Integer.parseInt((String) queryParams.get("status")) : null;
        this.dateType = Integer.valueOf((String) queryParams.getOrDefault("dateType", "1"));
        this.beginTime = queryParams.containsKey("beginTime") ? Long.parseLong((String) queryParams.get("beginTime")) : null;
        this.endTime = queryParams.containsKey("endTime") ? Long.parseLong((String) queryParams.get("endTime")) : null;
        this.searchText = (String) queryParams.getOrDefault("searchText", null);
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
