package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjIncentiveCreate extends CsjCreateBase{
    // 激励视屏

    // 渲染方式 1（模版渲染），2（自渲染） 这里只有模版渲染
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    private Integer renderType = 1;

    // 视频播放方向/屏幕方向 1是竖版；2是横版
    private Integer orientation = 1;

    // 是否在广告中显示奖励内容、 0：否；1：是，默认为否 设置为“是”时，奖励名称与奖励数量为必填
    @JSONField(name = "is_reward_retain_pop", alternateNames = {"isRewardRetainPop"})
    private Integer isRewardRetainPop = 0;

    // 奖励名称
    @JSONField(name = "reward_name", alternateNames = {"rewardName"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String rewardName;

    // 奖励数量
    @JSONField(name = "reward_count", alternateNames = {"rewardCount"})
    private Integer rewardCount;

    // 奖励发放设置 可选值：0（无需服务器判断）、1（需要服务器判断）
    @JSONField(name = "reward_is_callback", alternateNames = {"rewardIsCallback"})
    private Integer rewardIsCallback;

    // 回调URL 当奖励发放设置为需要服务器判断时必填，否则，该字段无效
    @JSONField(name = "reward_callback_url", alternateNames = {"rewardCallbackUrl"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String rewardCallbackUrl;
}
