package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class BdBase {
    // app应用 id
    @JSONField(name = "app_sid", alternateNames = {"appSid"})
    public String appSid;
}
