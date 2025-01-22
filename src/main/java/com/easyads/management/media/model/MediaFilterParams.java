package com.easyads.management.media.model;

import java.util.Map;

public class MediaFilterParams {
    private Map<String, Object> queryParams;

    // 筛选参数及翻页参数
    public String searchText;
    public Integer platform ;
    public Integer status;
    public Integer offset, limit, page;

    public MediaFilterParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        this.searchText = queryParams.containsKey("searchText") ? (String) queryParams.get("searchText") : null;
        this.platform = queryParams.containsKey("platform") ? Integer.valueOf((String) queryParams.get("platform")) : null;
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
