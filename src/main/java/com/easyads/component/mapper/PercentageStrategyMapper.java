package com.easyads.component.mapper;

import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.percentage.SdkPercentage;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentage;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public interface PercentageStrategyMapper {
    // 流量切分相关操作
    @MapKey("percentageId")
    Map<Integer, SdkPercentage> getTrafficPercentageMap(Integer adspotId);
    List<SdkPercentage> getTrafficPercentageList(Integer adspotId);
    int createOnePercentage(SdkPercentage trafficPercentage);
    int createPercentageList(List<SdkPercentage> trafficPercentageList);
    int updatePercentageList(List<SdkPercentage> trafficPercentageList);

    // 因为流量分组的改动更新流量分发信息
    int createPercentageTraffic(Integer adspotId, List<SdkPercentage> trafficPercentageList,
                                List<SdkGroupStrategy> sdkGroupStrategyList,
                                List<SdkTargetPercentage> sdkTargetPercentageList);
    int deletePercentageTrafficCascade(Set<Integer> percentageIdList);
}
