package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjCpmUpdate extends CsjBase {
    @JSONField(name = "ad_slot_id", alternateNames = {"adSlotId"})
    private Integer adSlotId;

    @JSONField(name = "site_id", alternateNames = {"appId"})
    private Integer appId;

    private String cpm;
    @JSONField(name = "delete_cpm", alternateNames = {"deleteCpm"})
    private Boolean deleteCpm = false;
}
