package com.easyads.management.experiment.report.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SdkExperimentGroup {
    private Integer groupId;
    private String tag; // groupName
    @JsonIgnore
    private Float percentage; // 分组占比
    private String percentageString;
    private Integer status;
    private SdkExperimentGroupReportData data; // AB测试报表数据
}
