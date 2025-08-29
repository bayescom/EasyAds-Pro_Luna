package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.easyads.component.CONST;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class BdCreateBase extends BdBase {
    // 代码位类型
    @JSONField(name = "ad_type", alternateNames = {"adType"})
    public int adType;

    // 代码位名称
    @JSONField(name = "ad_name", alternateNames = {"adName"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String adName;

    // 竞价模式 1 - 固价 2 - 实时竞价
    // 这个地方需要跟 广告源保持一致，
    @JSONField(name = "price_type", alternateNames = {"priceType"})
    public Integer priceType = 1;

    // cpm
    public Double cpm;

    // 代码位配置信息
    @JSONField(name = "ad_info", alternateNames = {"adInfo"})
    private BdAdInfo adInfo; // 直接使用Object接收

    public void setAdType (Integer adspotType) {
        // adspotType 1 横幅 2 开屏 3 插屏 6 信息流 12 激励视频
        if (adspotType == null) {
            throw new IllegalArgumentException("adspotType不能为null");
        }

        switch (adspotType) {
            case CONST.EASYADS_SPLASH:
                this.adType = CONST.BD_SPLASH;
                break;
            case CONST.EASYADS_INTERSTITIAL:
                this.adType = CONST.BD_INTERSTITIAL;
                break;
            case CONST.EASYADS_FEED:
                this.adType = CONST.BD_FEED;
                break;
            case CONST.EASYADS_INCENTIVE:
                this.adType = CONST.BD_INCENTIVE;
                break;
            default:
                throw new IllegalArgumentException("不支持的adspotType: " + adspotType);
        }
    }
}
