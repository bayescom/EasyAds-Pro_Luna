package com.easyads.export.model.format;

import lombok.Data;

@Data
public class SdkAdspotProperty {
    private String adspotid;
    private String appid;
    private AdspotRequestLimit requestLimit;
}
