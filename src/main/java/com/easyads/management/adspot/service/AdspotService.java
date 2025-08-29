package com.easyads.management.adspot.service;


import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.AdspotMapper;
import com.easyads.component.mapper.GroupStrategyMapper;
import com.easyads.component.mapper.PercentageStrategyMapper;
import com.easyads.component.mapper.SdkTrafficMapper;
import com.easyads.management.adspot.model.Adspot;
import com.easyads.management.adspot.model.AdspotFilterParams;

import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.percentage.SdkPercentage;
import com.easyads.management.distribution.traffic.model.SdkTrafficSingle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdspotService {

    @Autowired
    private AdspotMapper adspotMapper;

    @Autowired
    private PercentageStrategyMapper percentageStrategyMapper;

    @Autowired
    private GroupStrategyMapper groupStrategyMapper;

    @Autowired
    private SdkTrafficMapper sdkTrafficMapper;

    public Map<String, Object> getAdspotList(Map<String, Object> queryParams) throws BadRequestException {
        Map<String, Object> adspotResult = new HashMap(){{
            put("meta", new HashMap(){{put("total", 0);}});
            put("adspots", new ArrayList<>());
        }};

        AdspotFilterParams adspotFilterParams = new AdspotFilterParams(queryParams);

        int totalCount = adspotMapper.getAdspotCount(adspotFilterParams);
        ((Map) adspotResult.get("meta")).put("total", totalCount);
        adspotResult.put("adspots", adspotMapper.getAdspotList(adspotFilterParams));

        return adspotResult;
    }

    public Map<String, Object> getOneAdspot(Long adspotId) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("adspot", adspotMapper.getOneAdspot(adspotId));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> createOneAdspot(Adspot adspot) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        adspotMapper.createOneAdspot(adspot);
        Adspot createAdspot = adspotMapper.getOneAdspot(adspot.getId());
        resultMap.put("adspot", createAdspot);
        // 创建完广告后，需要创建聚合SDK的流量分组信息
        createAdspotSdkTrafficStrategy(createAdspot.getId());
        return resultMap;
    }

    private void createAdspotSdkTrafficStrategy(long adspotId) {
        // 聚合SDK需要创建流量百分比分组、流量分组信息，默认都是单一的
        // 创建流量百分比策略
        SdkPercentage sps = new SdkPercentage();
        percentageStrategyMapper.createOnePercentage(sps);
        // 创建流量分组策略
        SdkGroupStrategy sgs = new SdkGroupStrategy();
        groupStrategyMapper.createOneGroupStrategy(sgs);
        // 创建流量分发信息
        SdkTrafficSingle sdkTrafficSingle = new SdkTrafficSingle(adspotId, sps.getPercentageId(), sgs.getGroupTargetId());
        sdkTrafficMapper.createOnePercentageGroupStrategyTraffic(sdkTrafficSingle);
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneAdspot(long adspotId, Adspot adspot) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        adspotMapper.updateOneAdspot(adspotId, adspot);
        Adspot updatedAdspot = adspotMapper.getOneAdspot(adspotId);
        resultMap.put("adspot", updatedAdspot);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> deleteOneAdspot(Long adspotId) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        adspotMapper.deleteOneAdspot(adspotId);
        resultMap.put("adspot", null);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateMultiAdspotStatus(List<Adspot> adspotList) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        List<Adspot> updateAdspotList = new ArrayList<>();

        for(Adspot adspot : adspotList) {
            Long adspotId = adspot.getId();
            adspotMapper.updateOneAdspot(adspotId, adspot);
            Adspot updateAdspot = adspotMapper.getOneAdspot(adspotId);
            updateAdspotList.add(updateAdspot);
        }

        resultMap.put("adspots", updateAdspotList);

        return resultMap;
    }
}
