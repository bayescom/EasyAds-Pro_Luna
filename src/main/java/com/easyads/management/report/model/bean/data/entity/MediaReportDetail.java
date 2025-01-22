package com.easyads.management.report.model.bean.data.entity;

import com.easyads.component.utils.SystemUtils;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class MediaReportDetail extends MediaReport {
    public long id;
    public Integer platform;
    public String mediaId;
    public String mediaName;
    public String adspotId;
    public String adspotName;
    public String channelId;
    public String channelName;
    public String sdkAdspotId;

    public MediaReportDetail(String dateRange) {
        super(dateRange);
        this.id = 0;
        this.platform = null;
        this.mediaId = StringUtils.EMPTY;
        this.mediaName = StringUtils.EMPTY;
        this.adspotId = StringUtils.EMPTY;
        this.adspotName = StringUtils.EMPTY;
        this.channelId = StringUtils.EMPTY;
        this.channelName = StringUtils.EMPTY;
        this.sdkAdspotId = StringUtils.EMPTY;
    }

    public void completeInfo() {
        super.calcAllIndicator();
        this.platform = SystemUtils.getPlatformById(this.mediaId, this.adspotId);
        this.mediaName = SystemUtils.getMediaNameById(this.mediaId);
        this.adspotName = SystemUtils.getAdspotNameById(this.adspotId);
        this.channelName = SystemUtils.getSdkAdnNameById(this.channelId);
    }
}
