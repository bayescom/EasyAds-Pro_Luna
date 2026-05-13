package com.easyads.management.adn.model.filter;

import java.util.Map;

import com.easyads.component.utils.DataStringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class SdkAdnTrafficFilterParams {
   private Map<String, Object> queryParams;
    public Long channelId;
    // 这些是请求参数
    public List<Integer> mediaIds;
    public String searchText;
    private List<Integer> adspotTypes;
    public Integer status;
    public Integer offset, limit, page;
    public String sort;
    public boolean asc;

    public SdkAdnTrafficFilterParams(Map<String, Object> queryParams, long channelId) {
        this.queryParams = queryParams;
        this.channelId = channelId;
        this.mediaIds = queryParams.containsKey("mediaIds") ? DataStringUtils.stringExplodeIntegerList((String)queryParams.get("mediaIds"), ',') : null;
        this.status = queryParams.containsKey("status") ? Integer.parseInt((String) queryParams.get("status")) : null;
        this.searchText = (String) queryParams.getOrDefault("searchText", null);
        this.adspotTypes = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotTypes", null), ',');
        this.sort = queryParams.containsKey("sort") ? (String) queryParams.get("sort") : "sdkChannelId"; // 默认按照渠道ID排序
        this.asc = queryParams.containsKey("dir") ? "asc".equals(queryParams.get("dir")) : false; // 默认降序
        setLimit();
    }

    private void setLimit() {
        limit = queryParams.containsKey("limit") ? Integer.parseInt((String) queryParams.get("limit")) : 10;
        page = queryParams.containsKey("page") ? Integer.parseInt((String) queryParams.get("page")) : 1;
        if (limit != null && this.page != null) {
            offset = (page - 1) * limit;
        }
    }
}
