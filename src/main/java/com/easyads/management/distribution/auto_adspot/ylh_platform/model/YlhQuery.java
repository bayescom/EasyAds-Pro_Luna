package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class YlhQuery {
    @JSONField(name = "member_id", alternateNames = {"memberId"})
    public long memberId;
    public ArrayList<YlhQueryFilteringParams> filtering;

    public YlhQuery(long memberId, String placementId) {
        this.memberId = memberId;
        YlhQueryFilteringParams ylhQueryFilteringParams = getYlhQueryFilteringParams(placementId);
        ArrayList<YlhQueryFilteringParams> filtering = new ArrayList<>();
        filtering.add(ylhQueryFilteringParams);
        this.filtering = filtering;
    }

    @Data
    public static class YlhQueryFilteringParams {
        public String field = "placement_id";
        public String operator = "EQUALS";
        public List<String> values;
    }

    public YlhQueryFilteringParams getYlhQueryFilteringParams(String placementId) {
        YlhQueryFilteringParams ylhQueryFilteringParams = new YlhQueryFilteringParams();
        List<String> values = new ArrayList<>();
        values.add(placementId);
        ylhQueryFilteringParams.setValues(values);
        return ylhQueryFilteringParams;
    }
}
