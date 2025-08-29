package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YlhSplashCreate extends YlhCreateBase {
    // 开屏

    // 广告素材类型  两个字段必须同时传递，才能创建
    @JSONField(name = "ad_crt_type_list", alternateNames = {"adCrtTypeList"})
    public String[] adCrtTypeList;

    @JSONField(name = "flash_crt_type", alternateNames = {"flashCrtType"})
    public String flashCrtType = "FLASH";
}
