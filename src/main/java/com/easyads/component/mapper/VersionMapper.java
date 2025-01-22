package com.easyads.component.mapper;

import com.easyads.management.version.model.bean.Appver;
import com.easyads.management.version.model.bean.Sdkver;
import com.easyads.management.version.model.filter.AppverFilterParams;
import com.easyads.management.version.model.filter.SdkverFilterParams;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface VersionMapper {
    // Appver相关接口
    int getAppverCount(int type, AppverFilterParams filterParams);
    List<Appver> getAppverList(int type, AppverFilterParams filterParams);
    Appver getOneAppver(int type, Long id);
    void createOneAppver(int type, Appver appver);
    void updateOneAppver(Long id, Appver appver);

    // Sdkver相关接口
    int getSdkverCount(int type, SdkverFilterParams filterParams);
    List<Sdkver> getSdkverList(int type, SdkverFilterParams filterParams);
    Sdkver getOneSdkver(int type, Long id);
    void createOneSdkver(int type, Sdkver sdkver);
    void updateOneSdkver(Long id, Sdkver sdkver);
}
