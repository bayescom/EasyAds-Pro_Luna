package com.easyads.management.distribution.strategy.model.exp;

import lombok.Data;

@Data
public class SdkExperiment {
    private Integer expId;
    private Integer expType;
    private String expName;
    private String createdAt;
    private int status;
}