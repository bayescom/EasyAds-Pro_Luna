package com.easyads.management.distribution.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SdkGroup {
    private String group_tag;
    private long sdk_group_id;
    private long sdk_group_percentage_id;
    private long sdk_group_targeting_id;
    private long sdk_group_targeting_percentage_id;
}
