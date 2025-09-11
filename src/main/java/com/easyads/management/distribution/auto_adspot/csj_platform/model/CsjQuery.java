package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjQuery extends CsjBase {
    private Integer page = 1;

    @JSONField(name = "page_size", alternateNames = {"pageSize"})
    private Integer pageSize = 100;

    // 广告位id
    // 前端使用驼峰命名：adSlotId，转成 穿山甲需要的下划线 ad_slot_id 的形式，但是还是要使用 getAdSlotId 还能取到值
    @JSONField(name = "ad_slot_id", alternateNames = {"adSlotId"})
    private Integer adSlotId;
}
