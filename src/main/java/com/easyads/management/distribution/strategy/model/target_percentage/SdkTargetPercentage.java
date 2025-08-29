package com.easyads.management.distribution.strategy.model.target_percentage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SdkTargetPercentage {
    private Long targetPercentageId;
    private String tag;
    private Integer percentage;
    private Integer status;
    @JsonIgnore
    private int expId;
    @JsonIgnore
    private Long copyTargetPercentageId;

    public SdkTargetPercentage() {
        this.targetPercentageId = null;
        this.tag = "A";
        this.percentage = 100;
        this.status = 1; // 默认启用
        this.expId = 0; // 默认为0，表示不属于任何实验
    }
}
