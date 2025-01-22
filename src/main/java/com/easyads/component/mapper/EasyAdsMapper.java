package com.easyads.component.mapper;

import com.easyads.export.model.origin.SdkOriginInfo;
import com.easyads.export.model.format.SdkAdspotProperty;
import com.easyads.export.model.origin.SdkGroupStrategyOrigin;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface EasyAdsMapper {
    @MapKey("adspotid")
    Map<String, SdkAdspotProperty> getSdkAdspotProperty();

    List<SdkOriginInfo> getSdkSupplierConf();

    List<SdkGroupStrategyOrigin> getSdkGroupStrategyOrigin();
}