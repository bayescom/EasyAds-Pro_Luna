package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class BdQuery extends BdBase {
    private Integer page_no = 1;

    private Integer page_size = 100;

    // 广告位id
    @JSONField(name = "tu_ids", alternateNames = {"tuIds"})
    private List<Long> tuIds;
}
