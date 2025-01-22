package com.easyads.management.report.service;

import com.easyads.component.enums.FilterEnum;
import com.easyads.component.mapper.FilterMapper;
import com.easyads.component.mapper.MediaReportMapper;
import com.easyads.management.report.model.bean.filter.AdspotFilterUnit;
import com.easyads.management.report.model.bean.filter.ChannelFilterUnit;
import com.easyads.management.report.model.bean.filter.MediaFilterUnit;
import com.easyads.management.report.model.bean.filter.MetaAdspotFilterUnit;
import com.easyads.management.report.model.filter.FilterParams;
import com.easyads.management.report.model.filter.TrafficDataFilterParams;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrafficReportFilterService {
    @Autowired
    private FilterMapper filterMapper;

    @Autowired
    private MediaReportMapper mediaReportMapper;

    public Pair<TrafficDataFilterParams, TrafficDataFilterParams> genReportDataFilterParamsWithContrast(Map<String, Object> queryParams, FilterEnum filterEnum) {
        TrafficDataFilterParams dataFilterParams = new TrafficDataFilterParams(queryParams, filterEnum, false);
        TrafficDataFilterParams contrastDataFilterParams = null;
        if (queryParams.containsKey("contrastBeginTime") && queryParams.containsKey("contrastEndTime")) {
            contrastDataFilterParams = new TrafficDataFilterParams(queryParams, filterEnum, true);
        }
        return new MutablePair<>(dataFilterParams, contrastDataFilterParams);
    }

    // 统一获取指定时间段内ssp_report报表有数据的id
    private List<String> getReportHasDataIds(TrafficDataFilterParams dataFilterParams) {
        List<String> hasDataIds = new ArrayList<>();
        // 从mysql查天和小时报表
        if (dataFilterParams.getDailyBeginTime() != null || dataFilterParams.getHourlyBeginTime() != null) {
            hasDataIds = mediaReportMapper.getHasDataIds(dataFilterParams);
        }

        return hasDataIds;
    }

    // 查询时间或者对比查询时间内有数据的id
    private List<String> getReportHasDataIdsWithContrast(Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair) {
        TrafficDataFilterParams dataFilterParams = dataFilterParamsPair.getLeft();
        TrafficDataFilterParams contrastDataFilterParams = dataFilterParamsPair.getRight();
        List<String> hasDataIds = getReportHasDataIds(dataFilterParams);
        if (contrastDataFilterParams != null) {
            List<String> contrastHasDataIds = getReportHasDataIds(contrastDataFilterParams);
            // 去重，mysql/druid/对比都有重复的
            Set<String> s = new HashSet<>(hasDataIds);
            s.addAll(contrastHasDataIds);
            hasDataIds = new ArrayList<>(s);
        }
        return hasDataIds;
    }

    public Object getTrafficMedia(Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair, FilterParams filterParams) {
        List<String> hasDataIds = getReportHasDataIdsWithContrast(dataFilterParamsPair);
        filterParams.setHasDataIds(hasDataIds);
        List<MediaFilterUnit> mediaFilterUnitList = filterMapper.getValidMedia(filterParams);
        return mediaFilterUnitList;
    }

    public Object getTrafficAdspot(Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair, FilterParams filterParams) {
        List<String> hasDataIds = getReportHasDataIdsWithContrast(dataFilterParamsPair);
        filterParams.setHasDataIds(hasDataIds);
        List<AdspotFilterUnit> adspotFilterUnitList = filterMapper.getValidAdspot(filterParams);
        return adspotFilterUnitList;
    }

    public Object getTrafficChannel(Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair, FilterParams filterParams) {
        List<String> hasDataIds = getReportHasDataIdsWithContrast(dataFilterParamsPair);
        filterParams.setHasDataIds(hasDataIds);
        List<ChannelFilterUnit> channelFilterUnitList = filterMapper.getValidChannel(filterParams);

        return channelFilterUnitList;
    }

    public Object getTrafficMetaAdspot(Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair, FilterParams filterParams) {
        // 广告源的查询比较特殊，唯一id是一个"渠道id_广告源id"的字符串
        List<String> hasDataIds = getReportHasDataIdsWithContrast(dataFilterParamsPair);
        filterParams.setHasDataIds(hasDataIds);
        List<MetaAdspotFilterUnit> metaAdspotFilterUnitList = filterMapper.getValidMetaAdspot(filterParams);

        // 广告源只显示查询时间内有数据的，其他删掉
        if (dataFilterParamsPair.getLeft().getBeginTime() != null) {
            metaAdspotFilterUnitList.removeIf(cfu -> !hasDataIds.contains(String.valueOf(cfu.getChannelIdMetaAdspotIdStr())));
        }

        return metaAdspotFilterUnitList;
    }
}
