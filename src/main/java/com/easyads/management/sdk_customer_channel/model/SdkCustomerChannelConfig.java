package com.easyads.management.sdk_customer_channel.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class SdkCustomerChannelConfig {
    private Integer id;
    private Integer sdkCustomerChannelId;
    private Integer status;
    private Integer osType;
    private String config;

    public Object getConfigObj() {  // 字段名是 configObj
        if (config == null) return null;
        return JSON.parse(config);
    }
}
