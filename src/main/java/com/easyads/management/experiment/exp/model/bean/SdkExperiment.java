package com.easyads.management.experiment.exp.model.bean;

import lombok.Data;

@Data
public class SdkExperiment {
    private Long id;
    private Long adspotId;
    private String adspotName;
    private Long mediaId;
    private String mediaName;
    private String mediaIcon;
    //  A B实验类型， 1-流量切分， 2 - 策略流量切分
    private Integer experimentType;
    private String experimentName;
    private String createdAt;
    private String endAt;
    private int status;
}
