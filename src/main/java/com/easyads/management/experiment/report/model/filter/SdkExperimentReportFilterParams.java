package com.easyads.management.experiment.report.model.filter;

import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.SystemUtils;
import com.easyads.component.utils.TimeUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SdkExperimentReportFilterParams {
    private Map<String, Object> queryParams;

    // 筛选参数和翻页参数
    private Long companyId;
    private Integer adspotId;
    private Integer expId;
    private Integer expType;
    private Long beginTime;
    private Long endTime;
    private boolean isThird; // 是否是三方数据
    private List<String> display; // 下载勾选了哪些指标
    private String expName; // 给下载起文件名用

    // 计算出来的参数
    // dailyBeginTime - dailyEndTime这段时间从天表查，hourlyBeginTime - hourlyEndTime从小时表查
    private Long dailyBeginTime;
    private Long dailyEndTime;
    private Long hourlyBeginTime;
    private Long hourlyEndTime;


    public SdkExperimentReportFilterParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        this.adspotId = queryParams.containsKey("adspotId") ? Integer.parseInt((String) queryParams.get("adspotId")) : null;
        this.expId = queryParams.containsKey("expId") ? Integer.parseInt((String) queryParams.get("expId")) : null;
        this.expType = queryParams.containsKey("expType") ? Integer.parseInt((String) queryParams.get("expType")) : null;
        this.beginTime = queryParams.containsKey("beginTime") ? Long.parseLong((String) queryParams.get("beginTime")) : null;
        this.endTime = queryParams.containsKey("endTime") ? Long.parseLong((String) queryParams.get("endTime")) : null;
        this.isThird = queryParams.containsKey("isThird") && Integer.parseInt((String) queryParams.get("isThird")) == 1;
        this.display = DataStringUtils.stringExplodeList((String) queryParams.getOrDefault("display", null), ',');
        this.expName = queryParams.containsKey("expName") ? (String) queryParams.get("expName") : null;
        calcExtraParams();
    }

    private void calcExtraParams() {
        long dailyReportUpdateTime = SystemUtils.getMediaReportDailyMaxTimestamp();
        if (endTime < dailyReportUpdateTime + 3600 * 24) {
            dailyBeginTime = beginTime;
            dailyEndTime = endTime;
        } else {
            dailyBeginTime = beginTime;
            dailyEndTime = dailyReportUpdateTime + 3600 * 24 - 1;
            hourlyBeginTime = dailyEndTime + 1; // 小时表从天表的下一天开始
            hourlyEndTime = endTime;
        }
    }
}
