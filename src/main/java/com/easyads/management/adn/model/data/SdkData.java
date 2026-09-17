package com.easyads.management.adn.model.data;


import com.easyads.management.report.model.bean.data.entity.MediaReport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class SdkData extends MediaReport {
    @JsonIgnore
    // 这个sdkChannelId在查渠道数据的时候是渠道id，查单广告源的时候是CONCAT(channel_id, '_', sdk_adspot_id)
    public String sdkChannelId;

    public SdkData() {
        super(StringUtils.EMPTY);
    }

    public void completeIndicator() {
        super.calcAllIndicator();
    }
}
