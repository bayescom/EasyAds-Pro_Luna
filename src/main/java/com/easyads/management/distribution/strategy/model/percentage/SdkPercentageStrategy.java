package com.easyads.management.distribution.strategy.model.percentage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SdkPercentageStrategy {
    private Integer percentageId;
    private String tag;
    private Float percentage;
    @JsonIgnore
    private Integer copyPercentageId;

    public SdkPercentageStrategy() {
        this.percentageId = null;
        this.tag = "A";
        this.percentage = 100f;
    }
}
