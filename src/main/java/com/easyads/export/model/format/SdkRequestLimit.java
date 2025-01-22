package com.easyads.export.model.format;

import com.easyads.export.model.origin.SdkOriginInfo;
import lombok.Data;

@Data
public class SdkRequestLimit {
    private int daily_req_limit;
    private int daily_imp_limit;
    private int device_daily_req_limit;
    private int device_daily_imp_limit;
    private int device_request_interval;

    public SdkRequestLimit(SdkOriginInfo sdkOriginInfo) {
        this.daily_req_limit = sdkOriginInfo.getDaily_req_limit();
        this.daily_imp_limit = sdkOriginInfo.getDaily_imp_limit();
        this.device_daily_req_limit = sdkOriginInfo.getDevice_daily_req_limit();
        this.device_daily_imp_limit = sdkOriginInfo.getDevice_daily_imp_limit();
        this.device_request_interval = sdkOriginInfo.getDevice_request_interval();
    }
}
