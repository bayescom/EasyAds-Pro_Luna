package com.easyads.management.report.model.filter;


import com.easyads.component.utils.DataStringUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;

// 通用的筛选框筛选类
@Data
public class FilterParams {
    private String type; // channel里面 sql有部分依赖type
    private List<Integer> mediaIds;
    private List<Integer> channelIds; // 广告网络 -> 广告源
    private List<Integer> adspotTypes; // 媒体/广告位类型 -> 广告位
    private List<Integer> adspotIds;  // 广告位 -> 广告/创意

    // Service函数赋值，有数据的id
    private List<String> hasDataIds;

    public FilterParams(Map<String, Object> queryParams) {
        type = (String) queryParams.getOrDefault("type", null);
        if (queryParams.containsKey("mediaIds")) {
            mediaIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("mediaIds", null), ',');
        }
        if (queryParams.containsKey("channelIds")) {
            channelIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("channelIds", null), ',');
        }
        adspotTypes = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotTypes", null), ',');
        adspotIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotIds", null), ',');
    }
}
