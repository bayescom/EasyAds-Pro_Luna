package com.easyads.management.distribution.sdk.service;

import com.easyads.component.mapper.MediaReportMapper;
import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.component.mapper.SdkTrafficMapper;
import com.easyads.component.utils.ComparatorUtils;
import com.easyads.management.adn.model.data.ChannelTrafficDataFilter;
import com.easyads.management.adn.model.data.SdkData;
import com.easyads.management.adn.model.filter.SdkAdnTrafficFilterParams;
import com.easyads.management.distribution.sdk.model.SdkChannelMeta;
import com.easyads.management.distribution.sdk.model.SdkChannelTrafficSummary;
import com.easyads.management.distribution.sdk.model.SdkGroupTraffic;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

@Service
public class SdkAdnTrafficService {
    @Autowired
    private SdkTrafficMapper sdkTrafficMapper;

    @Autowired
    private MediaReportMapper mediaReportMapper;

    public Map<String, Object> getOneSdkChannelTraffic(long sdkChannelId, Map<String, Object> queryParams) throws Exception {
        Map<String, Object> trafficResult = new HashMap(){{
            put("meta", new HashMap(){{put("total", 0);}});
            put("sdk_channel_traffic", new ArrayList<>());
        }};

        SdkAdnTrafficFilterParams filterParams = new SdkAdnTrafficFilterParams(queryParams, sdkChannelId);

        List<SdkChannelTrafficSummary> sdkChannelList = sdkTrafficMapper.getOneSdkChannelTrafficList(filterParams);
        int count = sdkChannelList.size();

        if (CollectionUtils.isNotEmpty(sdkChannelList)) {
            Long beginTime = queryParams.containsKey("beginTime") ? Long.parseLong((String) queryParams.get("beginTime")) : null;
            Long endTime = queryParams.containsKey("endTime") ? Long.parseLong((String) queryParams.get("endTime")) : null;
            // 获取渠道的流量数据
            List<Integer> adspotIds = sdkChannelList.stream().map(SdkChannelTrafficSummary::getAdspotId).toList();
            String report_channel_id = String.valueOf(sdkChannelId);
            ChannelTrafficDataFilter dataFilter = new ChannelTrafficDataFilter(Integer.valueOf(report_channel_id), adspotIds, beginTime, endTime);
//            Map<String, SdkData> sdkChannelData = mediaReportMapper.getOneChannelMetaAdspotSdkTrafficData(dataFilter);
//            for (SdkChannelTrafficSummary sdkChannel : sdkChannelList) {
//                String sdkReportChannelId = sdkChannel.getReportChannelId() + "_" + sdkChannel.getSdkChannelParams().get("meta_adspot_id");
//                SdkData sdkData = sdkChannelData.get(sdkReportChannelId);
//                if (null != sdkData) {
//                    sdkData.completeIndicator();
//                    sdkChannel.setData(sdkData);
//                } else {
//                    sdkChannel.setData(new SdkData());
//                }
//            }
        }


        // 排序，根据不同的sort参数进行排序
        Comparator comparator = ComparatorUtils.getComparator(filterParams.sort, filterParams.asc);
        Collections.sort(sdkChannelList, comparator);

        // 截断获取要展示的渠道
        List<SdkChannelTrafficSummary> revealSdkChannelTrafficList = sdkChannelList;
        if(null != filterParams.offset && null != filterParams.limit) {
            revealSdkChannelTrafficList = sdkChannelList.subList(filterParams.offset, Math.min(filterParams.offset + filterParams.limit, count));
        }

        ((Map) trafficResult.get("meta")).put("total", count);
        trafficResult.put("sdk_channel_traffic", revealSdkChannelTrafficList);

        return trafficResult;
    }

    private void closeSdkChannelInSuppliers(List<List> suppliers, Long adspotSdkChannelId) {
        for(List each_group : suppliers) {
            each_group.removeIf(item -> {
                long v = Long.parseLong(item.toString());
                return v == adspotSdkChannelId;
            });
        }
    }

    public Object updateOneSdkChannelTrafficStatus(Long sdkGroupId, Long adspotSdkChannelId, Map<String, Object> queryParams) throws Exception {
        Map<String, Object> resultMap = new HashMap(){{
            put("message", "success");
        }};

        Integer status = queryParams.containsKey("status") ? Integer.parseInt((String) queryParams.get("status")) : null;
        if (null == status) {
            throw new Exception("status参数错误");
        }

        SdkGroupTraffic sdkGroupTraffic = sdkTrafficMapper.getSdkGroupTrafficById(sdkGroupId);
        if(null == sdkGroupTraffic) {
            throw new Exception("获取sdkGroup信息错误");
        }

        List<List> suppliers = JSONObject.parseArray(sdkGroupTraffic.getSupplier_ids(), List.class);
        if(0 == status) {
            // 关闭
            closeSdkChannelInSuppliers(suppliers, adspotSdkChannelId);
        } else {
            // 为什么打开时候也要先关闭呢？因为可能存在重复开启的情况
            // 所以先关闭再开启，保证唯一性
            closeSdkChannelInSuppliers(suppliers, adspotSdkChannelId);
            // 开启
            if (CollectionUtils.isEmpty(suppliers)) {
                suppliers.add(new ArrayList<>());
            }
            List firstGroup = suppliers.get(0);
            if (!firstGroup.contains(adspotSdkChannelId)) {
                firstGroup.add(adspotSdkChannelId);
            }
        }

        // 更新supplier_ids字段
        sdkGroupTraffic.setSupplier_ids(JSONObject.toJSONString(suppliers));

        sdkTrafficMapper.updateTrafficGroup(sdkGroupId, sdkGroupTraffic.getSupplier_ids());

        return resultMap;
    }
}
