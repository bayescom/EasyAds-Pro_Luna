package com.easyads.component.mapper;

import com.easyads.management.distribution.strategy.model.direction.DimensionTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DimensionMapper {
    // 自定义定向
    int createDimensionTarget(List<DimensionTarget> dimensionTargetList);
}
