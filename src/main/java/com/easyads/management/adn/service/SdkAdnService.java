package com.easyads.management.adn.service;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.MediaReportMapper;
import com.easyads.component.mapper.SdkAdnMapper;
import com.easyads.management.adn.model.bean.SdkAdn;
import com.easyads.management.adn.model.bean.SdkAdnReportApi;
import com.easyads.management.adn.model.data.ChannelDataFilter;
import com.easyads.management.adn.model.filter.SdkAdnFilterParams;
import com.easyads.management.adn.model.data.SdkData;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SdkAdnService {

    @Autowired
    private SdkAdnMapper sdkAdnMapper;

    @Autowired
    private MediaReportMapper mediaReportMapper;

    public Map<String, Object> getSdkAdnlList(Map<String, Object> queryParams) throws BadRequestException {
        Map<String, Object> channelResult = new HashMap(){{
            put("meta", new HashMap(){{put("total", 0);}});
            put("sdk_adns", new ArrayList<>());
        }};

        SdkAdnFilterParams filterParams = new SdkAdnFilterParams(queryParams);
        int sdkChannelCount = sdkAdnMapper.getSdkAdnCount(filterParams);
        List<SdkAdn> sdkAdnList = sdkAdnMapper.getSdkAdnList(filterParams);

        for(SdkAdn sdkAdn : sdkAdnList) {
            sdkAdn.setData(new SdkData());
        }

        // 获取渠道的流量数据
        ChannelDataFilter dataFilter = new ChannelDataFilter(null, filterParams.beginTime, filterParams.endTime);
        Map<String, SdkData> sdkChannelData = mediaReportMapper.getSdkChannelTrafficData(dataFilter);

        for(SdkAdn sdkAdn : sdkAdnList) {
            SdkData sdkData = sdkChannelData.get(sdkAdn.getAdnId().toString());
            if(null != sdkData) {
                sdkData.completeIndicator();
                sdkAdn.setData(sdkData);
            } else {
                sdkAdn.setData(new SdkData());
            }
        }

        ((Map) channelResult.get("meta")).put("total", sdkChannelCount);
        channelResult.put("sdk_adns", sdkAdnList);

        return channelResult;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = "easyadsDbTransactionManager")
    public Map<String, Object> updateSdkAdn(Long sdkAdnId, SdkAdn sdkAdn) throws BadRequestException {
        Map<String, Object> channelResult = new HashMap();

        // 获取当前渠道目前已经存在的companyChannelId集合
        Set<Integer> sdkChannelReportApiIdSet = sdkAdnMapper.getSdkAdnReportApiIdList(sdkAdnId);

        // 对渠道参数里面的meta信息进行转化，获得meta_key -> meta_id的映射关系，后面会使用到
        for(SdkAdnReportApi sari : sdkAdn.getReportApiParams()) {
            // 转化并补全ReportApi参数相关信息
            sari.completeDbBean();

            if(null == sari.getId()) {
                // 如果reportApiId为空，则是新增一个参数
                sdkAdnMapper.createSdkAdnOneReportApi(sdkAdnId, sari);
            } else if(sdkChannelReportApiIdSet.contains(sari.getId())) {
                // 如果是一个已经存在的则是更新该参数
                sdkAdnMapper.updateSdkAdnOneReportApi(sari);
                sdkChannelReportApiIdSet.remove(sari.getId());
            }
        }

        // 删除掉已经不存在的渠道
        if(CollectionUtils.isNotEmpty(sdkChannelReportApiIdSet)) {
            for(Integer sdkAdnReportApiId : sdkChannelReportApiIdSet) {
                sdkAdnMapper.deleteSdkAdnOneReportApi(sdkAdnId, sdkAdnReportApiId);
            }
        }

        channelResult.put("sdk_Adn", sdkAdnMapper.getOneSdkAdnReportApi(sdkAdnId));
        return channelResult;
    }
}
