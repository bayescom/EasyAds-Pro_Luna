package com.easyads.management.user.model.filter;

import java.util.Map;

public class UserFilterParams {
    private Map<String, Object> queryParams;

    // 筛选参数及翻页参数
    // 这些是请求参数
    public Integer userRoleType;
    public Integer roleType;
    public Integer status;
    public String searchText;
    public Integer offset, limit, page;

    public UserFilterParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        this.userRoleType = queryParams.containsKey("userRoleType") ? Integer.parseInt((String) queryParams.get("userRoleType")) : null;
        this.roleType = queryParams.containsKey("roleType") ? Integer.parseInt((String) queryParams.get("roleType")) : null;
        this.searchText = (String) queryParams.getOrDefault("searchText", null);
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
}
