package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YlhFeedCreate extends YlhCreateBase {
    // 信息流

    // 广告样式多样性探索
    @JSONField(name = "enable_experiment", alternateNames = {"enableExperiment"})
    public String enableExperiment;

    // 渲染方式
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    public String renderType;

    // 广告素材类型
    @JSONField(name = "ad_crt_type_list", alternateNames = {"adCrtTypeList"})
    public String[] adCrtTypeList;

    // 模版样式
    @JSONField(name = "ad_crt_template_type", alternateNames = {"adCrtTemplateType"})
    public String[] adCrtTemplateType;

    // 广告样式
    @JSONField(name = "ad_crt_normal_types", alternateNames = {"adCrtNormalTypes"})
    public String[] adCrtNormalTypes;
}
