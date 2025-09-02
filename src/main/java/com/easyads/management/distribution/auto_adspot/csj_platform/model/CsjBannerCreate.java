package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.easyads.component.enums.CsjBannerAdSlotSizeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjBannerCreate extends CsjCreateBase {
    // 横幅

    // 渲染方式 1（模版渲染），2（自渲染）这里只有模版渲染
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    private Integer renderType = 1;

    // 是否轮播 1（非轮播）、2（轮播）
    @JSONField(name = "slide_banner", alternateNames = {"slideBanner"})
    private Integer slideBanner;

    // 广告位尺寸 这个字段需要转为 width 和 height 前端传入的应该是一个int 的，我们需要转为 300 * 150 ，并赋值给 width 和 height
    private Integer adSlotSizeType;

    private Integer width;
    private Integer height;

    public void calWidth() {
        this.width =  CsjBannerAdSlotSizeTypeEnum.fromType(adSlotSizeType).getWidth();
    }

    public void calHeight() {
        this.height =  CsjBannerAdSlotSizeTypeEnum.fromType(adSlotSizeType).getHeight();
    }
}
