package com.easyads.management.distribution.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SdkConfig {
    private Integer isHeadBidding;
    private Float bidRatio;
}
