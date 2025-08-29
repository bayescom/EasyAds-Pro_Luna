package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import lombok.Data;

@Data
public class KsCpmUpdate {
    // 应用 id
    public String appId;
    // 广告位 id
    public Long positionId;
    // cpm 底价(元)整数, 注意，这个字段和新建的时候的字段不一样，新建的时候叫 expectCpm
    public Double cpmFloor;
}
