package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class BdFeedInfoFlowElement {
    //可选值：1（标题），2（描述），3图片），4（图标），5（视频）
    @JSONField(name = "element_groups", alternateNames = {"elementGroups"})
    private List<Integer> elementGgroups;

    @JSONField(name = "required_fields", alternateNames = {"requiredFields"})
    private List<Integer> requiredFields;

    //如果 element_groups 传参包含3（图片）但没有5（视频），也就是只要选中，7，15中任意一个，但是不选中75，就会有element_image_num字段；默认值为 1
    @JSONField(name = "element_image_num", alternateNames = {"elementImageNum"})
    private Integer elementImageNum;

    // 如果传了 element_image_num，必须传 element_size，其他情况下 element_size 可不传，默认值为 1
    @JSONField(name = "element_size", alternateNames = {"elementSize"})
    private Integer elementSize;
}
