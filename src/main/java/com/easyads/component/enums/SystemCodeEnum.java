package com.easyads.component.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum SystemCodeEnum {
    MEDIA_OS("媒体系统类型", 1),
    ADSPOT_TYPE("广告位类型", 2),
    USER_ROLE("用户角色类型", 3),
    LOCATION_DIRECTION("地域定向", 4),
    MAKE_DIRECTION("制造商定向", 5),
    OSV_DIRECTION("操作系统版本定向", 6),
    APPVER_DIRECTION("APP版本定向", 7),
    SDKVER_DIRECTION("SDK版本定向", 8);

    private String name;
    private int value;
}
