package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class YlhBase {
    // 媒体ID
    @JSONField(name = "app_id", alternateNames = {"appId"})
    public long appId;
    // 账号ID
    @JSONField(name = "member_id", alternateNames = {"memberId"})
    public long memberId;
}
