package com.easyads.management.distribution.strategy.service;

import com.easyads.component.enums.SdkExperimentEnum;
import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.*;
import com.easyads.management.distribution.strategy.model.exp.SdkExperiment;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentageExperiment;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentage;
import com.easyads.management.distribution.traffic.model.SdkTraffic;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
// TODO AB测试 类和其他变量都要改名
public class StrategyTargetPercentageService {
    @Autowired
    private GroupStrategyMapper groupStrategyMapper;

    @Autowired
    private PercentageStrategyMapper percentageStrategyMapper;

    @Autowired
    private TargetPercentageStrategyMapper sdkChannelPropertyMapper;

    @Autowired
    private SdkTrafficMapper sdkChannelTrafficMapper;

    @Autowired
    private SdkChannelExperimentMapper sdkChannelExperimentMapper;

    // TODO AB测试 SdkGroupStrategySummary这个类的构造函数依赖的utils，定向的名称映射map没有对应的表
    public Map<String, Object> getOneTargetStrategy(Long targetId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("strategySummary", groupStrategyMapper.getOneTargetStrategy(targetId));

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateTargetPercentage(Integer adspotId, Long percentageId, Long targetId,
                                                      SdkTargetPercentageExperiment sdkTargetPercentageExperiment) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int expType = SdkExperimentEnum.TARGET_PERCENTAGE.getValue();
        SdkExperiment sdkExperiment = sdkTargetPercentageExperiment.getExperiment();
        List<SdkTargetPercentage> sdkTargetPercentageList= sdkTargetPercentageExperiment.getTargetPercentageList();

        // 如果是新建实验，或者是修改实验的时候，需要先检查实验名是不是重复了
        if(0 == sdkExperiment.getExpId() || sdkTargetPercentageList.size() > 1) {
            SdkExperiment existSdkExperiment = sdkChannelExperimentMapper.getSdkExperimentByName(sdkExperiment.getExpName());
            if (null != existSdkExperiment && existSdkExperiment.getExpId().intValue() != sdkExperiment.getExpId().intValue()) {
                throw new BadRequestException("实验名称已存在，请修改实验名称");
            }
        }

        if(0 == sdkExperiment.getExpId()) {
            sdkChannelExperimentMapper.createSdkExperiment(sdkExperiment, adspotId, expType);
        }

        // 不管是怎么样的，都补充设置实验ID
        sdkTargetPercentageList.forEach(stp -> {
            stp.setExpId(sdkExperiment.getExpId());
        });

        // 当sdkTargetPercentageList只有一个组的时候，一般就是停止AB实验了只保留了一个组
        // 这个时候要把这个组的名字默认改成A组
        if(1 == sdkTargetPercentageList.size()) {
            sdkTargetPercentageList.get(0).setTag("A组");
            sdkTargetPercentageList.get(0).setExpId(0);
            sdkChannelExperimentMapper.closeSdkExperiment(sdkExperiment.getExpId());
        }

        // 如果是AB实验的分组，设置实验ID和AB标签
        sdkChannelExperimentMapper.updateSdkExperiment(sdkExperiment);

        // 获取已有的定向下的流量分组信息
        Map<Long, SdkTargetPercentage> existSdkTargetPercentageMap = sdkChannelPropertyMapper.getTargetPercentageMap(adspotId, percentageId, targetId);

        // 区分新增，更新，删除的流量分组信息
        // 新增分为两个组，一个是不复制分发策略进行默认构建，一个是指定copy某个流量分组的分发策略
        List<SdkTargetPercentage> addDefaultSdkTargetPercentageList = new ArrayList<>();
        List<SdkTargetPercentage> addCopySdkTargetPercentageList = new ArrayList<>();
        List<SdkTargetPercentage> updateSdkTargetPercentageList = new ArrayList<>();

        for(SdkTargetPercentage stp : sdkTargetPercentageList) {
            if(existSdkTargetPercentageMap.containsKey(stp.getTargetPercentageId())) {
                updateSdkTargetPercentageList.add(stp);
                SdkTargetPercentage removeSdkTargetPercentage = existSdkTargetPercentageMap.remove(stp.getTargetPercentageId());
            } else {
                if(null == stp.getCopyTargetPercentageId()) {
                    addDefaultSdkTargetPercentageList.add(stp);
                } else {
                    addCopySdkTargetPercentageList.add(stp);
                }
            }
        }

        // 删除没有的分组信息
        if(MapUtils.isNotEmpty(existSdkTargetPercentageMap)) {
            sdkChannelPropertyMapper.deleteTargetPercentageTrafficCascade(percentageId, targetId, existSdkTargetPercentageMap.keySet());
        }

        // 添加新的分组信息
        // 添加默认策略的分组
        if(CollectionUtils.isNotEmpty(addDefaultSdkTargetPercentageList)) {
            sdkChannelPropertyMapper.createTargetPercentage(addDefaultSdkTargetPercentageList);
            sdkChannelPropertyMapper.createTargetPercentageTraffic(adspotId, percentageId, targetId, addDefaultSdkTargetPercentageList);
        }

        // 添加copy策略的分组
        if(CollectionUtils.isNotEmpty(addCopySdkTargetPercentageList)) {
            sdkChannelPropertyMapper.createTargetPercentage(addCopySdkTargetPercentageList);

            Long copyTargetPercentageId = addCopySdkTargetPercentageList.get(0).getCopyTargetPercentageId();
            SdkTraffic sdkTargetTrafficList = sdkChannelTrafficMapper.getOneAdspotSdkTargetTrafficDetail(adspotId, percentageId, targetId, copyTargetPercentageId);
            String copySupplierTraffic = sdkTargetTrafficList.getTrafficGroupList().get(0).getTargetPercentageStrategyList().get(0).getSupplier_ids();
            sdkChannelPropertyMapper.createTargetPercentageTrafficWithSupplier(adspotId, percentageId, targetId, addCopySdkTargetPercentageList, copySupplierTraffic);
        }

        // 更新分组信息
        if(CollectionUtils.isNotEmpty(updateSdkTargetPercentageList)) {
            sdkChannelPropertyMapper.updateTargetPercentage(updateSdkTargetPercentageList);
        }

        // 返回更新后的定向策略信息
        resultMap.put("trafficPercentageList", percentageStrategyMapper.getTrafficPercentageList(adspotId));

        return resultMap;
    }
}
