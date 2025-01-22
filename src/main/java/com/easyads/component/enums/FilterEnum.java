package com.easyads.component.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum FilterEnum {
    MEDIA("媒体", 1),
    ADSPOT("广告位", 2),
    CHANNEL("广告网络", 3),
    META_ADSPOT("广告源", 4),
    MEDIA_ADSPOT("应用", 5),
    CUSTOMER_AD("广告", 6);

    private final String name;
    private final int value;
}
