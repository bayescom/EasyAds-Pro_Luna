package com.easyads.export.model;

import com.easyads.export.model.format.AdspotRequestLimit;
import com.easyads.export.model.format.SdkAdspotProperty;
import com.easyads.export.model.format.SdkFlowGroup;
import lombok.Getter;

import java.util.List;

@Getter
public class SdkAdspotConf {
    private String appid;
    private String adspotid;
    private AdspotRequestLimit request_limit;
    private List<SdkFlowGroup> group;

    public SdkAdspotConf(SdkAdspotProperty sdkAdspotProperty, List<SdkFlowGroup> sdkFlowGroupList) {
        this.appid = sdkAdspotProperty.getAppid();
        this.adspotid = sdkAdspotProperty.getAdspotid();
        this.request_limit = sdkAdspotProperty.getRequestLimit();
        this.group = sdkFlowGroupList;
    }
}
