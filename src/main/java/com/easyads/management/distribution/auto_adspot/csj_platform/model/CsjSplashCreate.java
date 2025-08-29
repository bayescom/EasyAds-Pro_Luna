package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjSplashCreate extends CsjCreateBase {
    // 开屏

    // 渲染方式 1（模版渲染），3（自渲染） 这里只有自渲染
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    private Integer renderType = 3;

    //屏幕方向 1（竖屏，默认为竖屏）、2（横屏）
    private Integer orientation = 1;

    // 创意交互形式、 1是“启用”，2是“不启用”
    @JSONField(name = "splash_shake", alternateNames = {"splashShake"})
    private Integer splashShake = 1;
}
