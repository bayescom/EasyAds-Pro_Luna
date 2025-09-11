package com.easyads.component.mapper;

import com.easyads.management.distribution.traffic.model.SdkChannelSimple;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.easyads.management.distribution.sdk.model.SdkChannelMeta;

import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface SdkChannelMapper {
    // 获取广告位上的SDK信息
    List<SdkChannel> getAdspotSdkChannelList(Long adspotId);
    // 获取广告位上SDK渠道的简易信息
    @MapKey("id")
    Map<Integer, SdkChannelSimple> getAdspotSdkSimpleChannelMap(Integer adspotId);

    // 获取广告位上仍在使用的SDK流量分组信息
    List<String> getAdspotSdkChannelUsingStatus(Integer adspotId, Integer sdkChannelId);

    // 创建&获取&编辑&删除某个广告位上的SDK渠道信息
    SdkChannel getOneAdspotSdkChannel(Long adspotId, Integer sdkChannelId);
    int createOneAdspotSdkChannel(Long adspotId, SdkChannel sdkChannel);
    int updateOneAdspotSdkChannel(Long adspotId, Integer sdkChannelId, SdkChannel sdkChannel);
    int updateOneAdspotSdkChannelSupplierAdspotConfig(Long adspotId, Integer sdkChannelId, SdkChannel sdkChannel);
    int deleteOneAdspotSdkChannel(Long adspotId, Integer sdkChannelId);

    // 获取指定广告位及SDK渠道下，获取常用的meta参数信息
    SdkChannelMeta getAdspotSdkChannelMeta(Long adspotId, Long sdkAdnId);
}
