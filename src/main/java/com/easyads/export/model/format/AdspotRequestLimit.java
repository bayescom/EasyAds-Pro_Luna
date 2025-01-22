package com.easyads.export.model.format;

import lombok.Data;

@Data
public class AdspotRequestLimit {
    private Integer device_daily_req_limit;
    private Integer device_daily_imp_limit;
    private Integer device_request_interval;
}
