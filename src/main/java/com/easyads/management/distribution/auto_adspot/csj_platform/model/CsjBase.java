package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class CsjBase {
    @JSONField(name = "user_id", alternateNames = {"userId"})
    public Integer userId;

    @JSONField(name = "role_id", alternateNames = {"roleId"})
    public Integer roleId;

    @JSONField(name = "security_key", alternateNames = {"securityKey"}, serialize = false)
    public String securityKey;

    public String sign;
    public int timestamp;
    public Integer nonce = 3;
    public String version = "1.0";

    @JSONField(name = "app_id", alternateNames = {"appId"})
    public Integer appId;
}
