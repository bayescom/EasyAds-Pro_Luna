package com.easyads.management.distribution.strategy.model.percentage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SdkPercentage {
    private Integer percentageId;
    private String tag;
    private Float percentage;
    private Integer status;
    @JsonIgnore
    private int expId;
    @JsonIgnore
    private Integer copyPercentageId;

    public SdkPercentage() {
        this.percentageId = null;
        this.tag = "A";
        this.percentage = 100f;
        this.status = 1; // 默认启用
        this.expId = 0; // 默认为0，表示不属于任何实验
    }
}
