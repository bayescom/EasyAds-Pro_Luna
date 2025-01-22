package com.easyads.management.report.model.bean.data.filter;

import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.SystemUtils;
import com.easyads.component.utils.TimeUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MediaReportFilterParams {
    public Map<String, Object> queryParams;

    // 筛选参数
    public Integer type;
    public Long beginTime;
    public Long endTime;
    public List<Integer> mediaIds;
    public List<Integer> adspotIds;
    public List<Integer> channelIds;
    public List<String> sdkAdspotIds;
    public List<Integer> adspotIdsByType;
    public List<String> dimensions;
    public List<String> display;
    public String indicator;
    public Integer offset, limit, page;
    public String sort, dir;

    // 计算出来的参数
    public String table;
    public boolean hasTimestampDimension;
    public List<String> selectDimensions;
    public boolean hasChannelDimension;
    public List<String> groupbyDimensions;

    // 是否按天/周查询的时候，查询了今天的数据或者凌晨的时候查了昨天还没完成的数据
    public boolean hasTodayDailyData = false;
    // beginTime - dailyEndTime这段时间从天表查，hourlyBeginTime - endTime从小时表查
    // 如果没有需要从小时表查的数据，dailyEndTime == endTime
    public Long dailyEndTime;
    public Long hourlyBeginTime;

    // 下载时候的参数
    public boolean isDownload;

    public MediaReportFilterParams(Map<String, Object> queryParams, boolean isContrast) {
        this.queryParams = queryParams;
        this.isDownload = queryParams.containsKey("isDownload") ? true : false;
        this.type = queryParams.containsKey("type") ? Integer.parseInt((String) queryParams.get("type")) : null;
        if(!isContrast) {
            this.beginTime = queryParams.containsKey("beginTime") ? Long.parseLong((String) queryParams.get("beginTime")) : null;
            this.endTime = queryParams.containsKey("endTime") ? Long.parseLong((String) queryParams.get("endTime")) : null;
        } else {
            this.beginTime = queryParams.containsKey("contrastBeginTime") ? Long.parseLong((String) queryParams.get("contrastBeginTime")) : null;
            this.endTime = queryParams.containsKey("contrastEndTime") ? Long.parseLong((String) queryParams.get("contrastEndTime")) : null;
        }
        this.mediaIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("mediaIds", null), ',');
        this.adspotIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotIds", null), ',');
        this.channelIds = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("channelIds", null), ',');
        this.sdkAdspotIds = DataStringUtils.stringExplodeList((String) queryParams.getOrDefault("sdkAdspotIds", null), ',');
        this.adspotIdsByType = getAdspotIdsByType(queryParams);
        this.dimensions = DataStringUtils.stringExplodeList((String) queryParams.getOrDefault("dimensions", null), ',');
        this.display = DataStringUtils.stringExplodeList((String) queryParams.getOrDefault("display", null), ',');
        this.indicator = queryParams.containsKey("indicator") ? (String) queryParams.getOrDefault("indicator", null) : null;
        if(!isContrast) {
            // 对比的时候不需要这些参数，被对比的需要这些，对比的不需要这些
            this.sort = queryParams.containsKey("sort") ? (String) queryParams.get("sort") : null;
            this.dir = queryParams.containsKey("dir") ? (String) queryParams.get("dir") : null;
            setLimit();
        }
        calcExtraParams();
    }

    private void setLimit() {
        limit = queryParams.containsKey("limit") ? Integer.parseInt((String) queryParams.get("limit")) : null;
        page = queryParams.containsKey("page") ? Integer.parseInt((String) queryParams.get("page")) : null;
        if (limit != null && this.page != null) {
            offset = (page - 1) * limit;
        }
    }

    private List<Integer> getAdspotIdsByType(Map<String, Object> queryParams) {
        List<Integer> adspotTypes = DataStringUtils.stringExplodeIntegerList((String) queryParams.getOrDefault("adspotTypes", null), ',');
        if(CollectionUtils.isEmpty(adspotTypes)) {
            return null;
        }

        List<Integer> adspotIds = getAdspotIdsByAdspotType(adspotTypes);

        if(CollectionUtils.isEmpty(adspotIds)) {
            // 这里因为通过广告位类型筛选，如果没有对应的广告位，那么查询就要写死为-1，这样就不会查出任何数据
            return Arrays.asList(-1);
        } else {
            return adspotIds;
        }
    }

    private List<Integer> getAdspotIdsByAdspotType(List<Integer> adspotTypes) {
        List<Integer> adspotIds = SystemUtils.getAdspotIdsByTypeList(adspotTypes);
        return adspotIds;
    }

    private void calcExtraParams() {
        this.table = 1 == this.type ? "report_hourly" : "report_daily";

        // time dimension
        List<String> timeDimensions = new ArrayList<>();
        if(1 == this.type) {
            timeDimensions.add("timestamp AS dimension");
            timeDimensions.add("FROM_UNIXTIME(MIN(timestamp), '%Y-%m-%d %H时') AS dateRange");
        } else if(2 == this.type) {
            timeDimensions.add("DATE_FORMAT(from_unixtime(`timestamp`), '%Y%m%d') AS dimension");
            timeDimensions.add("FROM_UNIXTIME(MIN(timestamp), '%Y-%m-%d') AS dateRange");
        } else {
            timeDimensions.add("YEARWEEK(FROM_UNIXTIME(timestamp)) AS dimension");
            timeDimensions.add("CONCAT(FROM_UNIXTIME(MIN(timestamp), '%Y-%m-%d'), ' - ', FROM_UNIXTIME(MAX(timestamp), '%Y-%m-%d')) AS dateRange");
        }

        this.hasTimestampDimension = CollectionUtils.isEmpty(this.dimensions) ? true : this.dimensions.contains("timestamp");
        if(CollectionUtils.isNotEmpty(this.dimensions) && this.hasTimestampDimension) {
            this.dimensions.remove("timestamp");
        }

        // 设置select dimension
        this.selectDimensions = new ArrayList<>();
        if(this.hasTimestampDimension) {
            this.selectDimensions.addAll(timeDimensions);
        } else {
            this.selectDimensions.add("0 AS dimension");
            this.selectDimensions.add("'' AS dateRange");
        }

        // 设置groupby 的 dimension
        this.groupbyDimensions = new ArrayList<>();
        if(this.hasTimestampDimension) {
            this.groupbyDimensions.add("dimension");
        }

        // 补充设置select groupby 的 dimension
        this.hasChannelDimension = CollectionUtils.isNotEmpty(this.channelIds);
        for(String dim : dimensions) {
            String dimensionUnderlineName;
            dimensionUnderlineName = DataStringUtils.camel2underline(dim);
            this.selectDimensions.add(String.format("%s AS %s", dimensionUnderlineName, dim));
            this.groupbyDimensions.add(dimensionUnderlineName);
        }

        // 如果选了分天/分周而且选了当天/昨天没出来的数据，需要从小时表查询一部分数据
        if (type != 1) {
            // 接口自己补的参数
            Long latestDailyReportTimeStamp = (Long) queryParams.getOrDefault("latestDailyReportTimeStamp", null);
            // 如果结束时间的 当天的开始时间 > 最新的report时间戳，那么需要查小时表
            if (endTime - 3600 * 24 + 1  > latestDailyReportTimeStamp) {
                hasTodayDailyData = true;
                dailyEndTime = latestDailyReportTimeStamp + 3600 * 24 - 1;
                hourlyBeginTime = dailyEndTime + 1;
            } else {
                dailyEndTime = endTime;
            }
        } else {
            dailyEndTime = endTime;
        }
    }

    public boolean isParamsInvalid() {
        return null == type || null == this.beginTime || null == this.endTime;
    }

    public List<String> calcTimeRangeList() {
        return TimeUtils.calcTimeRangeList(this.type, this.beginTime, this.endTime);
    }

    public List<String> calcWeekOfYearList() {
        return TimeUtils.calcWeekOfYearList(this.beginTime, this.endTime);
    }
}