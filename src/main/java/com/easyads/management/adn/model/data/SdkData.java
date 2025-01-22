package com.easyads.management.adn.model.data;


import com.easyads.management.report.model.bean.data.entity.MediaReport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class SdkData extends MediaReport {
    @JsonIgnore
    public String sdkChannelId;

    public SdkData() {
        super(StringUtils.EMPTY);
    }

    public void completeIndicator() {
        super.calcAllIndicator();
    }
}
