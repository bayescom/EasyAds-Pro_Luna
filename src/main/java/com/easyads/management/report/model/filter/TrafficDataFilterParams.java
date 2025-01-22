package com.easyads.management.report.model.filter;


import com.easyads.component.enums.FilterEnum;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TrafficDataFilterParams {
    private String type;
    private List<Integer> mediaIds;

    private Long beginTime;
    private Long endTime;
    // 报表根据查询时间可能需要查三部分数据：天报表、小时报表、实时
    private Long dailyBeginTime;
    private Long dailyEndTime;
    private Long hourlyBeginTime;
    private Long hourlyEndTime;

    // Service函数赋值，用来指定通用模版查数据库哪个字段（媒体、广告位、广告网络...）
    private String mysqlField;

    public TrafficDataFilterParams(Map<String, Object> queryParams, FilterEnum filterEnum, boolean isContrast) {
        calcExtraParams(filterEnum);
    }

    private void calcExtraParams(FilterEnum filterEnum) {
        // 计算时间参数，查三部分数据
        // 天报表: dailyBeginTime - dailyEndTime
        // 小时报表: hourlyBeginTime - hourlyEndTime
        // 实时系统: realtimeBeginTime - realtimeEndTime

        switch (filterEnum) {
            case MEDIA:
                mysqlField = "media_id";
                break;
            case ADSPOT:
                mysqlField = "adspot_id";
                break;
            case CHANNEL:
                mysqlField = "channel_id";
                break;
            case META_ADSPOT:
                mysqlField = "CONCAT(channel_id, '_', sdk_adspot_id)";
                break;
        }
    };
}
