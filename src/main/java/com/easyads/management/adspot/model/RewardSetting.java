package com.easyads.management.adspot.model;

import lombok.Data;

@Data
public class RewardSetting {
    private Integer rewardAmount;
    private String rewardName;
    private String rewardCallback;
    private String securityKey;
}
