package com.easyads.component.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum DirectionTypeEnum {
    DELIVERY_AD("广告", 1),
    SUPPLIER("API渠道", 2),
    SDK_GROUP("SDK分组", 3),
    SDK_SUPPLIER("SDK渠道", 4);

    private String name;
    private int value;
}
