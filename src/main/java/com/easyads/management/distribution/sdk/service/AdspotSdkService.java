package com.easyads.management.distribution.sdk.service;


import com.easyads.component.mapper.MediaReportMapper;
import com.easyads.component.mapper.SdkAdnMapper;
import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.component.mapper.SdkTrafficMapper;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.adn.model.bean.SdkAdnReportApi;
import com.easyads.management.adn.model.data.ChannelDataFilter;
import com.easyads.management.adn.model.data.SdkData;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.easyads.management.distribution.traffic.model.SdkTrafficGroupSimple;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdspotSdkService {

    @Autowired
    private SdkChannelMapper sdkChannelMapper;

    @Autowired
    private SdkTrafficMapper sdkTrafficMapper;

    @Autowired
    private SdkAdnMapper sdkAdnMapper;

    @Autowired
    private MediaReportMapper mediaReportMapper;

    public Map<String, Object> getOneAdspotSdkChannelList(Map<String, Object> queryParams, Long adspotId) throws Exception {
        Map<String, Object> sdkResult = new HashMap();
        Long beginTime = queryParams.containsKey("beginTime") ? Long.parseLong(queryParams.get("beginTime").toString()) : null;
        Long endTime = queryParams.containsKey("endTime") ? Long.parseLong(queryParams.get("endTime").toString()) : null;

        List<SdkChannel> sdkChannelList = sdkChannelMapper.getAdspotSdkChannelList(adspotId);
        ChannelDataFilter dataFilter = new ChannelDataFilter(adspotId, beginTime, endTime);
        Map<String, SdkData> sdkChannelDataMap = mediaReportMapper.getSdkChannelTrafficData(dataFilter);
        for(SdkChannel sdkChannel : sdkChannelList) {
            String sdkChannelId = sdkChannel.getReportChannelId() + "_" + sdkChannel.getParams().getAdspotId();
            SdkData sdkData = sdkChannelDataMap.get(sdkChannelId);
            if(null != sdkData) {
                sdkData.completeIndicator();
                sdkChannel.setData(sdkData);
            } else {
                sdkChannel.setData(new SdkData());
            }
        }

        sdkResult.put("sdkChannelList", sdkChannelList);
        return sdkResult;
    }

    public Map<String, Object> getOneAdspotSdkChannelUsingStatus(Integer adspotId, Integer sdkChannelId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> usingGroup = sdkChannelMapper.getAdspotSdkChannelUsingStatus(adspotId, sdkChannelId);
        resultMap.put("count", usingGroup.size());
        resultMap.put("groupList", usingGroup);

        return resultMap;
    }

    public Map<String, Object> getOneAdspotOneSdkChannel(Long adspotId, Integer sdkChannelId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        SdkChannel sdkChannel = sdkChannelMapper.getOneAdspotSdkChannel(adspotId, sdkChannelId);
        resultMap.put("sdkChannel", sdkChannel);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> createOneAdspotSdkChannel(Long adspotId, SdkChannel sdkChannel) throws Exception {
        sdkChannel.completeDbBean();

        // 创建SDK渠道的Report API
        SdkAdnReportApi reportApiParam = sdkChannel.getReportApiParam();
        if(null != reportApiParam) {
            if(reportApiParam.needCreate()) {
                // id为null，并且channel_params不为空就需要重新创建一个新的
                reportApiParam.completeDbBean();
                sdkAdnMapper.createSdkAdnOneReportApi(sdkChannel.getAdnId(), reportApiParam);
            }
        }

        // 创建渠道
        sdkChannelMapper.createOneAdspotSdkChannel(adspotId, sdkChannel);

        return getOneAdspotOneSdkChannel(adspotId, sdkChannel.getId());
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> createMultiAdspotSdkChannel(Long adspotId, List<SdkChannel> sdkChannelList) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<SdkChannel> createdSdkChannelList = new ArrayList<>();
        for(SdkChannel sdkChannel : sdkChannelList) {
            sdkChannel.completeDbBean();

            // 创建SDK渠道的Report API
            SdkAdnReportApi reportApiParam = sdkChannel.getReportApiParam();
            if(null != reportApiParam) {
                if(reportApiParam.needCreate()) {
                    // id为null，并且channel_params不为空就需要重新创建一个新的
                    reportApiParam.completeDbBean();
                    sdkAdnMapper.createSdkAdnOneReportApi(sdkChannel.getAdnId(), reportApiParam);
                }
            }

            // 创建渠道
            sdkChannelMapper.createOneAdspotSdkChannel(adspotId, sdkChannel);

            SdkChannel newSdkChannel = sdkChannelMapper.getOneAdspotSdkChannel(adspotId, sdkChannel.getId());
            createdSdkChannelList.add(newSdkChannel);
        }

        resultMap.put("sdkChannelList", createdSdkChannelList);

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneAdspotOneSdkChannel(Long adspotId, Integer sdkChannelId, SdkChannel sdkChannel) throws Exception {
        sdkChannel.completeDbBean();

        // 创建SDK渠道的Report API
        SdkAdnReportApi reportApiParam = sdkChannel.getReportApiParam();
        if(null != reportApiParam) {
            if(reportApiParam.needCreate()) {
                // id为null，并且channel_params不为空就需要重新创建一个新的
                reportApiParam.completeDbBean();
                sdkAdnMapper.createSdkAdnOneReportApi(sdkChannel.getAdnId(), reportApiParam);
            }
        }

        sdkChannelMapper.updateOneAdspotSdkChannel(adspotId, sdkChannelId, sdkChannel);

        return getOneAdspotOneSdkChannel(adspotId, sdkChannel.getId());
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public void deleteOneAdspotOneSdkChannel(Long adspotId, Integer sdkChannelId) throws Exception {
        // 从SDK渠道配置中删除
        sdkChannelMapper.deleteOneAdspotSdkChannel(adspotId, sdkChannelId);
        SdkChannel sdkChannel = sdkChannelMapper.getOneAdspotSdkChannel(adspotId, sdkChannelId);

        // 获取所有带该SDK的流量分发，并把分发中的这些给删除掉
        List<SdkTrafficGroupSimple> sdkTrafficGroupSimpleList = sdkTrafficMapper.getOneAdspotSdkTrafficSimple(adspotId, sdkChannelId);
        if(CollectionUtils.isNotEmpty(sdkTrafficGroupSimpleList)) {
            for (SdkTrafficGroupSimple stgs : sdkTrafficGroupSimpleList) {
                List<List> newSupplierTrafficList = new ArrayList<>();
                List<List> supplierTrafficList = JsonUtils.convertJsonToList(stgs.getSupplier_ids(), List.class);
                for (List oneLevelSupplier : supplierTrafficList) {
                    if (oneLevelSupplier.contains(sdkChannelId)) {
                        oneLevelSupplier.remove(sdkChannelId);
                    }
                    if (CollectionUtils.isNotEmpty(oneLevelSupplier)) {
                        newSupplierTrafficList.add(oneLevelSupplier);
                    }
                }
                stgs.setSupplier_ids(JsonUtils.toJsonString(newSupplierTrafficList));
            }

            // 从流量分发中删掉SDK渠道后，强行更新到数据库
            sdkTrafficMapper.updateOneAdspotAllTraffic(sdkTrafficGroupSimpleList);
        }
    }
}
