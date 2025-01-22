package com.easyads.component.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum TimeTypeEnum {
    TIME("时间", (short)1),
    HOUR("小时", (short)2),
    DAY("天", (short)3);

    private String name;
    private Short value;
}
