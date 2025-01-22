package com.easyads.management.distribution.sdk.service;

import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.management.distribution.sdk.model.SdkChannelMeta;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdspotSdkChannelService {
    @Autowired
    private SdkChannelMapper sdkChannelMapper;

    public Map<String, Object> getAdspotSdkChannelMetaAppInfo(Long adspotId, Long sdkChannelId) throws Exception {
        Map<String, Object> metaAppResult = new HashMap(){{
            put("app_id", null);
            put("app_key", null);
        }};

        SdkChannelMeta sdkChannelMeta = sdkChannelMapper.getAdspotSdkChannelMeta(adspotId, sdkChannelId);
        if (null != sdkChannelMeta && null != sdkChannelMeta.getSdkChannelParams()) {
            if(StringUtils.isNotBlank(sdkChannelMeta.getSdkChannelParams().getMediaId())) {
                metaAppResult.put("app_id", sdkChannelMeta.getSdkChannelParams().getMediaId());;
            }
            if(StringUtils.isNotBlank(sdkChannelMeta.getSdkChannelParams().getMediaKey())) {
                metaAppResult.put("app_key", sdkChannelMeta.getSdkChannelParams().getMediaKey());
            }
        }

        return metaAppResult;
    }
}
