package com.easyads.management.sdk_customer_channel.model;

import lombok.Data;

import java.util.List;

@Data
public class SdkCustomerChannel {
    private Integer id;
    private String name;

    // 非数据库字段，用于返回前端聚合数据
    private List<SdkCustomerChannelConfig> osConfigs;
}
