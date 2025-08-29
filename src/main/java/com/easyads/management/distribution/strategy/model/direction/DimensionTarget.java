package com.easyads.management.distribution.strategy.model.direction;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DimensionTarget {
    private Long id;
    private Long modelId;
    private int modelType;
    private int operator;
    private int dimensionId;
    private int dimensionValueSource;
    private String oldDimensionValue;
    private String dimensionValue;
    private String dimensionValueDetails;

    public DimensionTarget(Long modelId, int modelType, int operator, int dimensionId, int dimensionValueSource,
                           String dimensionValue, String dimensionValueDetails) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.operator = operator;
        this.dimensionId = dimensionId;
        this.dimensionValueSource = dimensionValueSource;
        this.dimensionValue = dimensionValue;
        this.dimensionValueDetails = dimensionValueDetails;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s", dimensionId, 0 == operator ? "包含" : "排除", dimensionValue);
    }
}