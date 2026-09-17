package com.easyads.management.distribution.sdk.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@Data
public class ChannelConfig {
    private String key;
    private String name;
    private String value;
    private Integer required;

    public ChannelConfig(String key, String name) {
        this.key = key;
        this.name = name;
        this.value = StringUtils.EMPTY;
        this.required = 1; // 默认必填
    }

    public ChannelConfig(String key, String name, Integer required) {
        this.key = key;
        this.name = name;
        this.value = StringUtils.EMPTY;
        this.required = required;
    }
}
