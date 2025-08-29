package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YlhBannerCreate extends YlhCreateBase {
    // 横幅

    // 渲染方式
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    public String renderType;

    // 广告样式
    @JSONField(name = "ad_crt_normal_types", alternateNames = {"adCrtNormalTypes"})
    public String[] adCrtNormalTypes;

    // 两个字段同时传，代表图文组合
    @JSONField(name = "ad_crt_type_list", alternateNames = {"adCrtTypeList"})
    public String[] adCrtTypeList;
    @JSONField(name = "ad_crt_template_type", alternateNames = {"adCrtTemplateType"})
    public String[] adCrtTemplateType;
}
