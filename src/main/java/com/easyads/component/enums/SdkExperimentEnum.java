package com.easyads.component.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum SdkExperimentEnum {
    GROUP_PERCENTAGE("流量分组实验", (short)1),
    TARGET_PERCENTAGE("瀑布流分组实验", (short)2);

    private String name;
    private Short value;
}
