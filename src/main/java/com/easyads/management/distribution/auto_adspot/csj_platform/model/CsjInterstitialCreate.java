package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjInterstitialCreate extends CsjCreateBase {
    // 插屏

    // 渲染方式 1（模版渲染），2（自渲染） 这里只有模版渲染
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    private Integer renderType = 1;

    // 广告铺开大小 1是全屏，2是半屏，3是优选
    @JSONField(name = "ad_rollout_size", alternateNames = {"adRolloutSize"})
    private Integer adRolloutSize = 1;

    // 广告素材类型、 1是仅图片；2是仅视频；3是图片+视频
    @JSONField(name = "accept_material_type", alternateNames = {"acceptMaterialType"})
    private Integer acceptMaterialType = 1;

    // 视频播放方向/屏幕方向 1是竖版；2是横版
    private Integer orientation = 1;

    // 视频自动播放、 1是“wifi下自动播放”，2是“有网络自动播放”，3是“不自动播放”
    @JSONField(name = "video_auto_play", alternateNames = {"videoAutoPlay"})
    private Integer videoAutoPlay = 1;

    // 视频声音、 1是“静音”；2是“有声音”
    @JSONField(name = "video_voice_control", alternateNames = {"videoVoiceControl"})
    private Integer videoVoiceControl = 1;

    // n秒后显示跳过按钮 全屏数值范围为5-15s；插屏数值范围为0-15s
    @JSONField(name = "skip_duration", alternateNames = {"skipDuration"})
    private Integer skipDuration;
}
