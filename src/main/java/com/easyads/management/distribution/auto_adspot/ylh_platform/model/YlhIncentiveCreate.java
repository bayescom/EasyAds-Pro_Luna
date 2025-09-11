package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YlhIncentiveCreate extends YlhCreateBase {
    // 激励视频

    // 渲染样式
    @JSONField(name = "rewarded_video_crt_type", alternateNames = {"rewardedVideoCrtType"})
    public String rewardedVideoCrtType;

    // 服务器判断
    @JSONField(name = "need_server_verify", alternateNames = {"needServerVerify"})
    public String needServerVerify;

    // 如需服务器判断，则以下必填
    // 回调url
    @JSONField(name = "transfer_url", alternateNames = {"transferUrl"})
    public String transferUrl;

    // 校验密钥 前端随机生成
    public String secret;
}
