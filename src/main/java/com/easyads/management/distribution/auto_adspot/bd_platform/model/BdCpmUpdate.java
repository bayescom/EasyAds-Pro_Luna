package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class BdCpmUpdate {
    // 代码位ID
    @JSONField(name = "tu_id", alternateNames = {"tuId"})
    private Long tuId;

    private Double cpm;
}
