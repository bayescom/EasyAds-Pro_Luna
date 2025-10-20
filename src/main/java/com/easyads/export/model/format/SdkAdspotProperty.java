package com.easyads.export.model.format;

import com.easyads.management.adspot.model.AdspotProperty;
import lombok.Data;

@Data
public class SdkAdspotProperty {
    private String adspotid;
    private String appid;
    private AdspotRequestLimit requestLimit;
    /*广告位属性信息*/
    private AdspotProperty property;
}
