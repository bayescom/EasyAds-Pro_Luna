package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.easyads.component.CONST;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YlhCreateBase extends YlhBase {
    // 广告位名称
    @JSONField(name = "placement_name", alternateNames = {"placementName"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String placementName;

    // 广告位ID
    @JSONField(name = "placement_id", alternateNames = {"placementId"})
    public long placementId;

    // 广告位类型
    public String scene;

    // 接入方式
    @JSONField(name = "ad_pull_mode", alternateNames = {"adPullMode"})
    public String adPullMode = "SDK";

    // 价格策略
    @JSONField(name = "price_strategy_type", alternateNames = {"priceStrategyType"})
    public String priceStrategyType;
    // 实时竞价类型
    @JSONField(name = "real_time_bidding_type", alternateNames = {"realTimeBiddingType"})
    public String realTimeBiddingType;
    // 目标价 价格
    @JSONField(name = "ecpm_price", alternateNames = {"ecpmPrice"})
    public Long ecpmPrice;

    public void setYlhScene (Integer adspotType) {
        // adspotType 1 横幅 2 开屏 3 插屏 6 信息流 12 激励视频
        if (adspotType == null) {
            throw new IllegalArgumentException("adspotType不能为null");
        }

        switch (adspotType) {
            case CONST.EASYADS_BANNER:
                this.scene = CONST.YLH_BANNER;
                break;
            case CONST.EASYADS_SPLASH:
                this.scene = CONST.YLH_SPLASH;
                break;
            case CONST.EASYADS_INTERSTITIAL:
                this.scene = CONST.YLH_INTERSTITIAL;
                break;
            case CONST.EASYADS_FEED:
                this.scene = CONST.YLH_FEED;
                break;
            case CONST.EASYADS_INCENTIVE:
                this.scene = CONST.YLH_INCENTIVE;
                break;
            default:
                throw new IllegalArgumentException("不支持的adspotType: " + adspotType);
        }
    }

    /** 设置媒体ID、账号ID、scene */
    public void setBaseParams(long appId, Long memberId, Integer adspotType) {
        this.appId = appId;
        this.memberId = memberId;
        this.setYlhScene(adspotType);
    }
}
