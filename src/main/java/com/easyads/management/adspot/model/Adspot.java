package com.easyads.management.adspot.model;

import lombok.Data;

@Data
public class Adspot {
    private Long id;
    private String adspotName;
    private Integer adspotType;
    private String adspotTypeName;
    private Integer platformType;
    private Long mediaId;
    private String mediaName;
    private String bundleName;
    private Integer status;
    /*单设备每日请求上限*/
    private Integer deviceDailyReqLimit;
    /*单设备每日曝光上限*/
    private Integer deviceDailyImpLimit;
    /*单设备最小请求间隔*/
    private Integer deviceReqInterval;
    /*广告位超时时间*/
    private Integer timeout;
}
