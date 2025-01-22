package com.easyads.management.adn.model.bean;

import com.easyads.management.adn.model.data.SdkData;
import lombok.Data;

import java.util.List;

@Data
public class SdkAdn {
    private Integer adnId;
    private String adnName;
    private int status;
    private List<ParamMeta> adnParamsMeta;
    private List<ParamMeta> reportApiParamsMeta;
    private List<SdkAdnReportApi> reportApiParams;
    private int reportApiStatus;
    private SdkData data;
}
