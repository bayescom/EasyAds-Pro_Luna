package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YlhInterstitialCreate extends YlhCreateBase {
    // 插屏

    // 渲染方式 后端默认必传
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    public String renderType = "TEMPLATE";

    // 渲染样式
    @JSONField(name = "ad_crt_template_type", alternateNames = {"adCrtTemplateType"})
    public String[] adCrtTemplateType;

    // 广告素材类型
    @JSONField(name = "ad_crt_type_list", alternateNames = {"adCrtTypeList"})
    public String[] adCrtTypeList;

    // 广告样式多样性探索
    @JSONField(name = "enable_experiment", alternateNames = {"enableExperiment"})
    public String enableExperiment;
}
