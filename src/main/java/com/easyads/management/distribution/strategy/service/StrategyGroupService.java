package com.easyads.management.distribution.strategy.service;

import com.easyads.component.mapper.GroupStrategyMapper;
import com.easyads.component.mapper.SdkTrafficMapper;
import com.easyads.component.mapper.TargetPercentageStrategyMapper;
import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StrategyGroupService {

    @Autowired
    private GroupStrategyMapper groupStrategyMapper;

    @Autowired
    private TargetPercentageStrategyMapper targetPercentageStrategyMapper;

    @Autowired
    private SdkTrafficMapper sdkTrafficMapper;

    public Map<String, Object> getOnePercentageGroupStrategy(Integer adspotId, Integer percentageId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        List<SdkGroupStrategy> sdkGroupStrategyList = groupStrategyMapper.getAllGroupStrategyList(adspotId, percentageId);
        resultMap.put("groupStrategyList", sdkGroupStrategyList);

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOnePercentageGroupStrategy(Integer adspotId, Integer percentageId,
                                                                List<SdkGroupStrategy> sdkGroupStrategyList) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Map<Long, SdkGroupStrategy> existSdkGroupStrategyMap = groupStrategyMapper.getAllGroupStrategyMap(adspotId, percentageId);

        List<SdkGroupStrategy> addSdkGroupStrategyList = new ArrayList<>();
        List<SdkGroupStrategy> updateSdkGroupStrategyList = new ArrayList<>();
        Map<Long, SdkGroupStrategy> originSdkGroupStrategyMap = new HashMap<>();
        // 先对每个定向策略进行转化成写数据库需要的格式，并收集到哪些是update的，哪些是add的，哪些是需要delete的group_strategy
        for(SdkGroupStrategy sgs : sdkGroupStrategyList) {
            sgs.completeDbBean();
            if(existSdkGroupStrategyMap.containsKey(sgs.getGroupTargetId())) {
                updateSdkGroupStrategyList.add(sgs);
                SdkGroupStrategy originSdkGroupStrategy = existSdkGroupStrategyMap.remove(sgs.getGroupTargetId());
                originSdkGroupStrategyMap.put(sgs.getGroupTargetId(), originSdkGroupStrategy);
            } else {
                addSdkGroupStrategyList.add(sgs);
            }
        }

        // 删除去掉的group_strategy
        if(MapUtils.isNotEmpty(existSdkGroupStrategyMap)) {
            // 删掉分发策略的定向配置信息
            groupStrategyMapper.deleteGroupStrategyList(existSdkGroupStrategyMap.keySet());
            // 从流量分发管理中删掉分发策略信息
            sdkTrafficMapper.deleteGroupStrategyTraffic(existSdkGroupStrategyMap.keySet());
        }

        // 对新增的group_strategy进行插入
        if(CollectionUtils.isNotEmpty(addSdkGroupStrategyList)) {
            groupStrategyMapper.createGroupStrategyList(addSdkGroupStrategyList);
            // 创建流量分发策略下的分组信息，默认只有一个组
            List<SdkTargetPercentage> sdkTargetPercentageList = addSdkGroupStrategyList.stream()
                    .map(sgs -> new SdkTargetPercentage()).collect(Collectors.toList());
            targetPercentageStrategyMapper.createTargetPercentage(sdkTargetPercentageList);
            sdkTrafficMapper.createGroupStrategyTraffic(adspotId, percentageId, addSdkGroupStrategyList);
        }

        // 对更新的group_strategy进行更新
        if(CollectionUtils.isNotEmpty(updateSdkGroupStrategyList)) {
            groupStrategyMapper.updateGroupStrategyList(updateSdkGroupStrategyList);
        }

        // 返回更新后的定向策略信息
        resultMap.put("groupStrategyList", groupStrategyMapper.getAllGroupStrategyList(adspotId, percentageId));

        return resultMap;
    }
}
