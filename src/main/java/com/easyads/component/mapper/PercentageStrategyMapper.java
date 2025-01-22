package com.easyads.component.mapper;

import com.easyads.management.distribution.strategy.model.percentage.SdkPercentageStrategy;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public interface PercentageStrategyMapper {
    // 流量切分相关操作
    @MapKey("percentageId")
    Map<Integer, SdkPercentageStrategy> getTrafficPercentageMap(Integer adspotId);
    List<SdkPercentageStrategy> getTrafficPercentageList(Integer adspotId);
    int createOnePercentage(SdkPercentageStrategy trafficPercentage);
    int createPercentageList(List<SdkPercentageStrategy> trafficPercentageList);
    int updatePercentageList(List<SdkPercentageStrategy> trafficPercentageList);
    int deletePercentageList(Set<Integer> percentageIdList);
}
