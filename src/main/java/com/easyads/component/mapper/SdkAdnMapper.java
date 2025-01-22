package com.easyads.component.mapper;

import com.easyads.management.adn.model.bean.SdkAdn;
import com.easyads.management.adn.model.bean.SdkAdnReportApi;
import com.easyads.management.adn.model.filter.SdkAdnFilterParams;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface SdkAdnMapper {
    // SDK渠道的广告网络信息，实际上本没有这个信息这里有点凑出来这个意思了
    int getSdkAdnCount(SdkAdnFilterParams filterParams);
    List<SdkAdn> getSdkAdnList(SdkAdnFilterParams filterParams);
    SdkAdn getOneSdkAdnReportApi(long sdkAdnId);
    Set<Integer> getSdkAdnReportApiIdList(long sdkAdnId);

    // SDK渠道的Report Api信息
    int createSdkAdnOneReportApi(long sdkAdnId, SdkAdnReportApi sdkAdnReportApi);
    int updateSdkAdnOneReportApi(SdkAdnReportApi sdkAdnReportApi);
    int deleteSdkAdnOneReportApi(long sdkAdnId, long sdkAdnReportApiId);
}
