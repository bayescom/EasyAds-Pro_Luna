package com.easyads.management.experiment.report.model.bean;

import com.easyads.component.utils.SystemUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SdkExperimentGroupReportDownload extends SdkExperimentGroupReportData {
    private String mediaName;
    private Integer mediaId;
    private String adspotName;
    private Integer adspotId;
    private String groupName; // 组名(流量比例)，即tag + percentage

    // 把AB实验分组对象封装成下载对象
    public SdkExperimentGroupReportDownload(Integer adspotId, SdkExperimentGroup group) {
        super(group.getData());
        this.adspotId = adspotId;
        this.adspotName = SystemUtils.getAdspotNameById(String.valueOf(this.adspotId));
        this.mediaName = SystemUtils.getMediaNameById(String.valueOf(this.mediaId));
        this.mediaId = Integer.valueOf(SystemUtils.getMediaIdByAdspotId(String.valueOf(this.adspotId)));
        this.groupName = String.format("%s(%s)", group.getTag(), group.getPercentageString());
    }
}
