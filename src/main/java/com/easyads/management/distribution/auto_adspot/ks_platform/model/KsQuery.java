package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import lombok.Data;

@Data
public class KsQuery {
    private Integer page = 1;

    private Integer pageSize = 100;

    // 广告位id
    private Long positionId;
}
