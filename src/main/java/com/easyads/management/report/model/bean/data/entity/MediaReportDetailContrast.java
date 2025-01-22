package com.easyads.management.report.model.bean.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MediaReportDetailContrast {
    public long id;
    public Integer platform;
    public String mediaId;
    public String mediaName;
    public String adspotId;
    public String adspotName;
    public String channelId;
    public String channelName;
    public String sdkAdspotId;

    public Map<String, MediaReportIndicator> data;

    public MediaReportDetailContrast(MediaReportDetail mediaReport) {
        // 基础字段信息从summaryMediaReport中获取
        this.id = mediaReport.id;
        this.platform = mediaReport.platform;
        this.mediaId = mediaReport.mediaId;
        this.mediaName = mediaReport.mediaName;
        this.adspotId = mediaReport.adspotId;
        this.adspotName = mediaReport.adspotName;
        this.channelId = mediaReport.channelId;
        this.channelName = mediaReport.channelName;
        this.sdkAdspotId = mediaReport.sdkAdspotId;
        this.data = genMediaReportIndicatorMap(mediaReport);
    }

    public MediaReportDetailContrast(MediaReportDetail mediaReport, MediaReportDetail contrastMediaReport) {
        // 基础字段信息从summaryMediaReport中获取
        this.id = mediaReport.id;
        this.platform = mediaReport.platform;
        this.mediaId = mediaReport.mediaId;
        this.mediaName = mediaReport.mediaName;
        this.adspotId = mediaReport.adspotId;
        this.adspotName = mediaReport.adspotName;
        this.channelId = mediaReport.channelId;
        this.channelName = mediaReport.channelName;
        this.sdkAdspotId = mediaReport.sdkAdspotId;
        this.data = genMediaReportIndicatorMap(mediaReport, contrastMediaReport);
    }

    @JsonIgnore
    private static final List<String> indicatorList = Arrays.asList(
            "dateRange", "req", "bid", "bidWin", "imp", "click", "income",
            "bidRate", "bidWinRate", "impRate", "clickRate", "ecpm", "ecpc",
            "thirdReq", "thirdBid", "thirdImp", "thirdClick", "thirdIncome",
            "thirdBidRate", "thirdImpRate", "thirdClickRate", "thirdEcpm", "thirdEcpc",
            "gapReqPercent", "gapBidPercent", "gapImpPercent", "gapClickPercent");

    public static Map<String, MediaReportIndicator> genMediaReportIndicatorMap(MediaReportDetail mediaReport) {
        Map<String, MediaReportIndicator> resultMap = new LinkedHashMap<>();
        for(String indicator : indicatorList) {
            resultMap.put(indicator, new MediaReportIndicator(indicator, mediaReport));
        }

        return resultMap;
    }

    public static Map<String, MediaReportIndicator> genMediaReportIndicatorMap(MediaReportDetail mediaReport, MediaReportDetail contrastMediaReport) {
        Map<String, MediaReportIndicator> resultMap = new LinkedHashMap<>();
        for(String indicator : indicatorList) {
            resultMap.put(indicator, new MediaReportIndicator(indicator, mediaReport, contrastMediaReport));
        }

        return resultMap;
    }
}
