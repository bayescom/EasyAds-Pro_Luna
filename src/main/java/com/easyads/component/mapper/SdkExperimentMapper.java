package com.easyads.component.mapper;

import com.easyads.management.experiment.exp.model.bean.SdkExperiment;
import com.easyads.management.experiment.exp.model.bean.SdkExperimentFilterUnit;
import com.easyads.management.experiment.exp.model.filter.SdkExperimentFilterParams;
import com.easyads.management.experiment.report.model.bean.SdkExperimentGroup;
import com.easyads.management.experiment.report.model.filter.SdkExperimentReportFilterParams;
import com.easyads.management.report.model.bean.filter.AdspotFilterUnit;
import com.easyads.management.report.model.bean.filter.MediaFilterUnit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SdkExperimentMapper {
    // 拿到筛选的测试名称列表
    List<SdkExperimentFilterUnit> getSdkExperimentFilterList();
    // 拿到筛选的媒体列表
    List<MediaFilterUnit> getSdkExperimentFilterMediaList();
    // 拿到筛选的广告位列表
    List<AdspotFilterUnit> getSdkExperimentFilterAdspotList(SdkExperimentFilterParams filterParams);

    // 获取测试报表总数
    int getSdkExperimentCount(SdkExperimentFilterParams filterParams);
    // 获取测试报表详细列表
    List<SdkExperiment> getSdkExperimentList(SdkExperimentFilterParams filterParams);
    // 获得单个测试报表
    SdkExperiment getOneSdkExperimentByExpId(Long expId);

    // AB测试报表的接口
    // 获取某个测试的分组信息
    List<SdkExperimentGroup> getSdkGroupList(SdkExperimentReportFilterParams filterParams);
}
