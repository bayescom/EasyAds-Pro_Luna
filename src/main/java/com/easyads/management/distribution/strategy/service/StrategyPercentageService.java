package com.easyads.management.distribution.strategy.service;

import com.easyads.component.mapper.GroupStrategyMapper;
import com.easyads.component.mapper.PercentageStrategyMapper;
import com.easyads.component.mapper.SdkTrafficMapper;
import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.percentage.SdkPercentageStrategy;
import com.easyads.management.distribution.traffic.model.SdkTraffic;
import com.easyads.management.distribution.traffic.model.SdkTrafficGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StrategyPercentageService {
    @Autowired
    private PercentageStrategyMapper percentageStrategyMapper;

    @Autowired
    private GroupStrategyMapper groupStrategyMapper;

    @Autowired
    private SdkTrafficMapper sdkTrafficMapper;

    public Map<String, Object> getOneTrafficPercentageList(Integer adspotId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        List<SdkPercentageStrategy> trafficPercentageList = percentageStrategyMapper.getTrafficPercentageList(adspotId);
        resultMap.put("trafficPercentageList", trafficPercentageList);

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateTrafficPercentage(Integer adspotId, List<SdkPercentageStrategy> sdkTrafficPercentageList) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 当sdkTrafficPercentageList只有一个组的时候，一般就是停止AB实验了只保留了一个组
        // 这个时候要把这个组的名字默认改成A组
        if(1 == sdkTrafficPercentageList.size()) {
            sdkTrafficPercentageList.get(0).setTag("A组");
        }

        // 获取已有的流量分组信息
        Map<Integer, SdkPercentageStrategy> existSdkTrafficPercentageMap = percentageStrategyMapper.getTrafficPercentageMap(adspotId);

        // 区分新增，更新，删除的流量分组信息
        // 新增分为两个组，一个是不复制分发策略进行默认构建，一个是指定copy某个流量分组的分发策略
        List<SdkPercentageStrategy> addDefaultSdkTrafficPercentageList = new ArrayList<>();
        List<SdkPercentageStrategy> addCopySdkTrafficPercentageList = new ArrayList<>();
        List<SdkPercentageStrategy> updateSdkTrafficPercentageList = new ArrayList<>();
        Map<Integer, SdkPercentageStrategy> originSdkTrafficPercentageMap = new HashMap<>();
        for(SdkPercentageStrategy sps : sdkTrafficPercentageList) {
            if(existSdkTrafficPercentageMap.containsKey(sps.getPercentageId())) {
                updateSdkTrafficPercentageList.add(sps);
                SdkPercentageStrategy removeSdkTrafficPercentage = existSdkTrafficPercentageMap.remove(sps.getPercentageId());
                // 记录下原来的分组信息，用于操作记录比照记录
                originSdkTrafficPercentageMap.put(sps.getPercentageId(), removeSdkTrafficPercentage);
            } else {
                if(null == sps.getCopyPercentageId()) {
                    addDefaultSdkTrafficPercentageList.add(sps);
                } else {
                    addCopySdkTrafficPercentageList.add(sps);
                }
            }
        }

        // 删除没有的分组信息
        if(MapUtils.isNotEmpty(existSdkTrafficPercentageMap)) {
            percentageStrategyMapper.deletePercentageList(existSdkTrafficPercentageMap.keySet());
            sdkTrafficMapper.deletePercentageTraffic(existSdkTrafficPercentageMap.keySet());
        }

        // 添加新的分组信息
        // 添加默认策略的分组
        if(CollectionUtils.isNotEmpty(addDefaultSdkTrafficPercentageList)) {
            percentageStrategyMapper.createPercentageList(addDefaultSdkTrafficPercentageList);
            List<SdkGroupStrategy> defaultSdkGroupStrategyList = Collections.nCopies(addDefaultSdkTrafficPercentageList.size(), new SdkGroupStrategy());
            groupStrategyMapper.createGroupStrategyList(defaultSdkGroupStrategyList);
            sdkTrafficMapper.createPercentageTraffic(adspotId, addDefaultSdkTrafficPercentageList, defaultSdkGroupStrategyList);
        }

        // 添加copy策略的分组
        if(CollectionUtils.isNotEmpty(addCopySdkTrafficPercentageList)) {
            percentageStrategyMapper.createPercentageList(addCopySdkTrafficPercentageList);
            for(SdkPercentageStrategy sps : addCopySdkTrafficPercentageList) {
                List<SdkTraffic> sdkTrafficList = sdkTrafficMapper.getOneAdspotSdkTrafficDetail(adspotId, sps.getCopyPercentageId());
                SdkTraffic onePercentageSdkTraffic = sdkTrafficList.get(0);
                // 遍历TrafficGroupList 把每个group里面的strategy和supplier_ids都记录下来
                List<SdkGroupStrategy> sdkGroupStrategyList = new ArrayList<>();
                List<String> supplierTraffic = new ArrayList<>();
                for(SdkTrafficGroup stg : onePercentageSdkTraffic.getTrafficGroupList()) {
                    stg.getGroupStrategy().clearIdInfo();
                    sdkGroupStrategyList.add(stg.getGroupStrategy());
                    supplierTraffic.add(stg.getSupplier_ids());
                }

                // 分组策略表中的记录
                groupStrategyMapper.createGroupStrategyList(sdkGroupStrategyList);
                // 分发策略表中的记录
                sdkTrafficMapper.createGroupStrategyTrafficWithSupplier(adspotId, sps.getPercentageId(), sdkGroupStrategyList, supplierTraffic);
            }
        }

        // 更新分组信息
        if(CollectionUtils.isNotEmpty(updateSdkTrafficPercentageList)) {
            percentageStrategyMapper.updatePercentageList(updateSdkTrafficPercentageList);
        }

        // 返回更新后的定向策略信息
        resultMap.put("trafficPercentageList", percentageStrategyMapper.getTrafficPercentageList(adspotId));

        return resultMap;
    }
}
