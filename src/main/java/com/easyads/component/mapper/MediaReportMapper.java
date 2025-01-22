package com.easyads.component.mapper;

import com.easyads.management.adn.model.data.ChannelDataFilter;
import com.easyads.management.adn.model.data.SdkData;
import com.easyads.management.report.model.bean.data.entity.MediaReport;
import com.easyads.management.report.model.bean.data.entity.MediaReportDetail;
import com.easyads.management.report.model.bean.data.filter.MediaReportFilterParams;
import com.easyads.management.report.model.filter.TrafficDataFilterParams;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MediaReportMapper {
    // 获取详细的汇总结果
    MediaReportDetail getMediaReportSummary(MediaReportFilterParams filterParams);

    // 获取媒体报表详细数据
    Long getMediaReportCount(MediaReportFilterParams filterParams);
    List<MediaReportDetail> getMediaReportDetail(MediaReportFilterParams filterParams);

    // 获取媒体报表图表数据
    List<MediaReport> getMediaReport(MediaReportFilterParams filterParams);

    // 新的通用接口，查询特定时间段哪些媒体/广告位/广告网络/广告源有数据
    List<String> getHasDataIds(TrafficDataFilterParams trafficDataFilterParams);

    // 获取广告网络的数据
    @MapKey("sdkChannelId")
    Map<String, SdkData> getSdkChannelTrafficData(ChannelDataFilter dataFilter);
}
