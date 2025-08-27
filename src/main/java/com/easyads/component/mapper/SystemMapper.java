package com.easyads.component.mapper;

import com.easyads.management.common.KeyValue;
import com.easyads.management.common.SystemCode;
import com.easyads.management.dimension.bean.ChildDimension;
import com.easyads.management.dimension.bean.ParentDimension;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SystemMapper {
    // 系统码
    List<SystemCode> getSystemCodeList(int type);

    // 定向维度
    List<ChildDimension> getDimensionList(int type);
    List<ParentDimension> getParentDimensionList(int type);
    List<ChildDimension> getDimensionListByFilter(int type, String filter);

    // 获取媒体ID和媒体名称的映射
    List<KeyValue> getMediaIdNameMap();
    // 获取广告位类型的广告位id
    List<KeyValue> getAdspotTypeIdMap();
    // 获取广告位ID和广告位名称的映射
    List<KeyValue> getAdspotIdNameMap();
    // 获取渠道id和渠道名称的映射
    List<KeyValue> getSdkAdnIdNameMap();
    // 获取媒体广告位平台映射
    List<KeyValue> getMediaAdspotPlatformMap();
    // 获取广告位ID所属的媒体ID映射
    List<KeyValue> getAdspotIdMediaIdMap();

    // 获取媒体昨天天表数据状态
    int getYesterdayMediaReportDailyStatus();
    Long getMediaReportDailyMaxTimestamp();
}
