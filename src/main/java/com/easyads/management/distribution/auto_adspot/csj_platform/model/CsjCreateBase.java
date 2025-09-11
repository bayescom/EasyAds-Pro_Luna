package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.easyads.component.CONST;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjCreateBase extends CsjBase {

    // 代码位类型 1（信息流）、2（横幅）、3（开屏）、5（激励视频）、9（插屏）
    @JSONField(name = "ad_slot_type", alternateNames = {"adSlotType"})
    public Integer adSlotType;

    // 是否使用穿山甲聚合
    @JSONField(name = "use_mediation", alternateNames = {"useMediation"})
    public String useMediation = "0";

    // 代码位名称
    @JSONField(name = "ad_slot_name", alternateNames = {"adSlotName"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String adSlotName;

    // cpm 设置
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String cpm;

    public void setAdSlotType(Integer adspotType) {
        if (adspotType == null) {
            throw new IllegalArgumentException("adspotType不能为null");
        }

        switch (adspotType) {
            case CONST.EASYADS_BANNER:
                this.adSlotType = CONST.CSJ_BANNER;
                break;
            case CONST.EASYADS_SPLASH:
                this.adSlotType = CONST.CSJ_SPLASH;
                break;
            case CONST.EASYADS_INTERSTITIAL:
                this.adSlotType = CONST.CSJ_INTERSTITIAL;
                break;
            case CONST.EASYADS_FEED:
                this.adSlotType = CONST.CSJ_FEED;
                break;
            case CONST.EASYADS_INCENTIVE:
                this.adSlotType = CONST.CSJ_INCENTIVE;
                break;
            default:
                throw new IllegalArgumentException("不支持的广告位类型: " + adspotType);
        }
    }
}