package com.easyads.component.mapper;

import com.easyads.management.experiment.report.model.bean.SdkExperimentGroupReportData;
import com.easyads.management.experiment.report.model.filter.SdkExperimentReportFilterParams;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface SdkExperimentReportMapper {
    @MapKey("groupId")
    Map<Integer, SdkExperimentGroupReportData> getGroupReportDataMap(SdkExperimentReportFilterParams filterParams);

    List<SdkExperimentGroupReportData> getDailyGroupReportDataList(SdkExperimentReportFilterParams filterParams);
}
