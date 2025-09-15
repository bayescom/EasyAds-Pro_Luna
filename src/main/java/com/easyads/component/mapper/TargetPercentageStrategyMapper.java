package com.easyads.component.mapper;

import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentage;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TargetPercentageStrategyMapper {
    // 流量分发策略下的分组信息
    @MapKey("targetPercentageId")
    Map<Long, SdkTargetPercentage> getTargetPercentageMap(Integer adspotId, Long percentageId, Long targetId);
    int createOneTargetPercentage(SdkTargetPercentage targetPercentage);
    int createTargetPercentage(List<SdkTargetPercentage> targetPercentageList);
    int updateTargetPercentage(List<SdkTargetPercentage> targetPercentageList);

    // 因为分发策略的分组改动更新流量分发信息
    int createTargetPercentageTraffic(Integer adspotId, Long percentageId, Long targetId,
                                      List<SdkTargetPercentage> sdkTargetPercentageList);
    int createTargetPercentageTrafficWithSupplier(Integer adspotId, Long percentageId, Long targetId,
                                                  List<SdkTargetPercentage> sdkTargetPercentageList,
                                                  String supplierTraffic);
    int deleteTargetPercentageTrafficCascade(Long percentageId, Long targetId, Set<Long> targetPercentageIdList);
}
