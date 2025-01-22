package com.easyads.management.distribution.sdk.model;

import lombok.Data;

@Data
public class SdkRequestLimit {
    private int dailyReqLimit;
    private int dailyImpLimit;
    private int deviceDailyReqLimit;
    private int deviceDailyImpLimit;
    private int deviceRequestInterval;
}
