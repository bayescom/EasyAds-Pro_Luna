package com.easyads.management.distribution.strategy.service;

import com.easyads.component.enums.SdkExperimentEnum;
import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.*;
import com.easyads.management.distribution.strategy.model.exp.SdkExperiment;
import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.percentage.SdkPercentage;
import com.easyads.management.distribution.strategy.model.percentage.SdkTrafficPercentageExperiment;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentage;
import com.easyads.management.distribution.traffic.model.SdkTraffic;
import com.easyads.management.distribution.traffic.model.SdkTrafficGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
// TODO AB测试 mapper和中间对象改名
public class StrategyPercentageService {
    @Autowired
    private GroupStrategyMapper groupStrategyMapper;

    @Autowired
    private PercentageStrategyMapper percentageStrategyMapper;

    @Autowired
    private TargetPercentageStrategyMapper targetPercentageStrategyMapper;

    @Autowired
    private SdkTrafficMapper sdkChannelTrafficMapper;

    @Autowired
    private SdkChannelExperimentMapper sdkChannelExperimentMapper;

    public Map<String, Object> getOneTrafficPercentageList(Integer adspotId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        List<SdkPercentage> trafficPercentageList = percentageStrategyMapper.getTrafficPercentageList(adspotId);
        resultMap.put("trafficPercentageList", trafficPercentageList);

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateTrafficPercentage(Integer adspotId, SdkTrafficPercentageExperiment sdkTrafficPercentageExperiment) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int expType = SdkExperimentEnum.GROUP_PERCENTAGE.getValue();
        SdkExperiment sdkExperiment = sdkTrafficPercentageExperiment.getExperiment();
        List<SdkPercentage> sdkTrafficPercentageList = sdkTrafficPercentageExperiment.getTrafficPercentageList();

        // 如果是新建实验，或者是修改实验的时候，需要先检查实验名是不是重复了
        if(0 == sdkExperiment.getExpId() || sdkTrafficPercentageList.size() > 1) {
            SdkExperiment existSdkExperiment = sdkChannelExperimentMapper.getSdkExperimentByName(sdkExperiment.getExpName());
            if (null != existSdkExperiment && existSdkExperiment.getExpId().intValue() != sdkExperiment.getExpId().intValue()) {
                throw new BadRequestException("实验名称已存在，请修改实验名称");
            }
        }

        if(0 == sdkExperiment.getExpId()) {
            sdkChannelExperimentMapper.createSdkExperiment(sdkExperiment, adspotId, expType);
        }

        // 不管是怎么样的，都补充设置实验ID
        sdkTrafficPercentageList.forEach(stp -> {
            stp.setExpId(sdkExperiment.getExpId());
        });

        // 当sdkTrafficPercentageList只有一个组的时候，一般就是停止AB实验了只保留了一个组
        // 这个时候要把这个组的名字默认改成A组
        if(1 == sdkTrafficPercentageList.size()) {
            sdkTrafficPercentageList.get(0).setTag("A组");
            sdkTrafficPercentageList.get(0).setExpId(0);
            sdkChannelExperimentMapper.closeSdkExperiment(sdkExperiment.getExpId());
        }

        // 如果是AB实验的分组，设置实验ID和AB标签
        sdkChannelExperimentMapper.updateSdkExperiment(sdkExperiment);

        // 获取已有的流量分组信息
        Map<Integer, SdkPercentage> existSdkTrafficPercentageMap = percentageStrategyMapper.getTrafficPercentageMap(adspotId);

        // 区分新增，更新，删除的流量分组信息
        // 新增分为两个组，一个是不复制分发策略进行默认构建，一个是指定copy某个流量分组的分发策略
        List<SdkPercentage> addDefaultSdkTrafficPercentageList = new ArrayList<>();
        List<SdkPercentage> addCopySdkTrafficPercentageList = new ArrayList<>();
        List<SdkPercentage> updateSdkTrafficPercentageList = new ArrayList<>();
        Map<Integer, SdkPercentage> originSdkTrafficPercentageMap = new HashMap<>();
        for(SdkPercentage stp : sdkTrafficPercentageList) {
            if(existSdkTrafficPercentageMap.containsKey(stp.getPercentageId())) {
                updateSdkTrafficPercentageList.add(stp);
                SdkPercentage removeSdkTrafficPercentage = existSdkTrafficPercentageMap.remove(stp.getPercentageId());
                // 记录下原来的分组信息，用于操作记录比照记录
                originSdkTrafficPercentageMap.put(stp.getPercentageId(), removeSdkTrafficPercentage);
            } else {
                if(null == stp.getCopyPercentageId()) {
                    addDefaultSdkTrafficPercentageList.add(stp);
                } else {
                    addCopySdkTrafficPercentageList.add(stp);
                }
            }
        }

        // 删除没有的分组信息
        if(MapUtils.isNotEmpty(existSdkTrafficPercentageMap)) {
            percentageStrategyMapper.deletePercentageTrafficCascade(existSdkTrafficPercentageMap.keySet());
        }

        // 添加新的分组信息
        // 添加默认策略的分组
        if(CollectionUtils.isNotEmpty(addDefaultSdkTrafficPercentageList)) {
            percentageStrategyMapper.createPercentageList(addDefaultSdkTrafficPercentageList);
            List<SdkGroupStrategy> defaultSdkGroupStrategyList = addDefaultSdkTrafficPercentageList.stream()
                            .map(x -> new SdkGroupStrategy()).collect(Collectors.toList());
            groupStrategyMapper.createGroupStrategyList(defaultSdkGroupStrategyList);
            // 创建流量分发策略下的分组信息，默认只有一个组
            List<SdkTargetPercentage> sdkTargetPercentageList = defaultSdkGroupStrategyList.stream()
                    .map(sgs -> new SdkTargetPercentage()).collect(Collectors.toList());
            targetPercentageStrategyMapper.createTargetPercentage(sdkTargetPercentageList);
            percentageStrategyMapper.createPercentageTraffic(adspotId, addDefaultSdkTrafficPercentageList, defaultSdkGroupStrategyList, sdkTargetPercentageList);
        }
        // 添加copy策略的分组
        if(CollectionUtils.isNotEmpty(addCopySdkTrafficPercentageList)) {
            percentageStrategyMapper.createPercentageList(addCopySdkTrafficPercentageList);
            for(SdkPercentage stp : addCopySdkTrafficPercentageList) {
                List<SdkTraffic> sdkTrafficList = sdkChannelTrafficMapper.getOneAdspotSdkTrafficDetail(adspotId, stp.getCopyPercentageId());
                SdkTraffic onePercentageSdkTraffic = sdkTrafficList.get(0);
                // 遍历TrafficGroupList 把每个group里面的strategy和supplier_ids都记录下来
                List<SdkGroupStrategy> sdkGroupStrategyList = new ArrayList<>();
                List<String> supplierTraffic = new ArrayList<>();
                for(SdkTrafficGroup stg : onePercentageSdkTraffic.getTrafficGroupList()) {
                    stg.getGroupStrategy().clearIdInfo();
                    sdkGroupStrategyList.add(stg.getGroupStrategy());
                    supplierTraffic.add(stg.getTargetPercentageStrategyList().get(0).getSupplier_ids());
                }

                groupStrategyMapper.createGroupStrategyList(sdkGroupStrategyList);
                // 创建流量分发策略下的分组信息，默认只有一个组
                List<SdkTargetPercentage> sdkTargetPercentageList = sdkGroupStrategyList.stream()
                        .map(sgs -> new SdkTargetPercentage()).collect(Collectors.toList());
                targetPercentageStrategyMapper.createTargetPercentage(sdkTargetPercentageList);
                sdkChannelTrafficMapper.createGroupStrategyTrafficWithSupplier(adspotId, stp.getPercentageId(), sdkGroupStrategyList, sdkTargetPercentageList, supplierTraffic);
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
