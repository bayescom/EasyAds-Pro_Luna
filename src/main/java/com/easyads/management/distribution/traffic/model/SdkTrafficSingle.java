package com.easyads.management.distribution.traffic.model;

import lombok.Data;

@Data
public class SdkTrafficSingle {
    public Long id;
    public Long adspotId;
    public Integer percentageId;
    public Long groupTargetId;
    public Long targetPercentageId;

    public SdkTrafficSingle(Long adspotId, Integer percentageId, Long groupTargetId, Long targetPercentageId) {
        this.adspotId = adspotId;
        this.percentageId = percentageId;
        this.groupTargetId = groupTargetId;
        this.targetPercentageId = targetPercentageId;
    }
}
