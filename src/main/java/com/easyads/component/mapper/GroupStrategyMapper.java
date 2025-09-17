package com.easyads.component.mapper;

import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategySummary;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public interface GroupStrategyMapper {
    // 流量分组策略信息
    @MapKey("groupTargetId")
    Map<Long, SdkGroupStrategy> getAllGroupStrategyMap(Integer adspotId, Integer percentageId);
    List<SdkGroupStrategy> getAllGroupStrategyList(Integer adspotId, Integer percentageId);
    int createOneGroupStrategy(SdkGroupStrategy sdkGroupStrategy);
    int createGroupStrategyList(List<SdkGroupStrategy> sdkGroupStrategyList);
    int updateGroupStrategyList(List<SdkGroupStrategy> sdkGroupStrategyList);
    int deleteGroupStrategyList(Set<Long> groupTargetIdList);
    SdkGroupStrategySummary getOneTargetStrategy(Long groupTargetId);
}
