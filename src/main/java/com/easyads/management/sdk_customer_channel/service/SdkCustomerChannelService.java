package com.easyads.management.sdk_customer_channel.service;

import com.easyads.component.enums.SystemCodeEnum;
import com.easyads.component.mapper.SdkCustomerChannelMapper;
import com.easyads.component.mapper.SystemMapper;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannel;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelConfig;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelFilterParams;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SdkCustomerChannelService {
    @Autowired
    private SdkCustomerChannelMapper sdkCustomerChannelMapper;

    @Autowired
    private SystemMapper systemMapper;

    public Map<String, Object> getSdkCustomerChannelList(Map<String, Object> queryParams) throws Exception {
        Map<String, Object> channelResult = new HashMap(){{
            put("meta", new HashMap(){{put("total", 0);}});
            put("sdk_customer_channels", new ArrayList<>());
        }};

        SdkCustomerChannelFilterParams filterParams = new SdkCustomerChannelFilterParams(queryParams);
        List<SdkCustomerChannel> sdkCustomerChannelList = sdkCustomerChannelMapper.getSdkCustomerChannelList(filterParams);

        int sdkCustomerChannelCount = sdkCustomerChannelMapper.getSdkCustomerChannelCount();

        ((Map) channelResult.get("meta")).put("total", sdkCustomerChannelCount);
        channelResult.put("sdk_customer_channels", sdkCustomerChannelList);

        return channelResult;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Object createOneSdkCustomerChannel(SdkCustomerChannel sdkCustomerChannel) throws Exception {
        sdkCustomerChannelMapper.createOneSdkCustomerChannel(sdkCustomerChannel);
        Integer channelId = sdkCustomerChannel.getId();

        List<SdkCustomerChannelConfig> osConfigs = sdkCustomerChannel.getOsConfigs();
        if (osConfigs != null && !osConfigs.isEmpty()) {
            for (SdkCustomerChannelConfig osConfig : osConfigs) {
                osConfig.setSdkCustomerChannelId(channelId);
            }

            sdkCustomerChannelMapper.batchInsertSdkCustomerChannelConfig(osConfigs);
        }

        return getOneSdkCustomerChannel(sdkCustomerChannel.getId());
    }

    public Map<String, Object> getOneSdkCustomerChannel(long id) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sdk_customer_channel", sdkCustomerChannelMapper.getOneSdkCustomerChannel(id));
        return resultMap;
    }

    public Map<String, Object> getAdapterAdspotType() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("code-list", systemMapper.getSystemCodeList(SystemCodeEnum.ADAPTER_ADSPOT_TYPE.getValue()));

        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Object updateOneSdkCustomerChannel(Long id, SdkCustomerChannel sdkCustomerChannel) throws Exception {
        if (sdkCustomerChannel.getId() == null) {
            // 新建
            sdkCustomerChannelMapper.createOneSdkCustomerChannel(sdkCustomerChannel);
        } else {
            // 更新，校验是否存在
            SdkCustomerChannel existing = sdkCustomerChannelMapper.getOneSdkCustomerChannel(sdkCustomerChannel.getId());
            if (existing == null) {
                throw new Exception("广告网络不存在");
            }
            sdkCustomerChannelMapper.updateOneSdkCustomerChannel(sdkCustomerChannel);
        }

        // 批量 upsert 配置
        List<SdkCustomerChannelConfig> osConfigs = sdkCustomerChannel.getOsConfigs();
        if (CollectionUtils.isNotEmpty(osConfigs)) {
            for (SdkCustomerChannelConfig config : osConfigs) {
                config.setSdkCustomerChannelId(sdkCustomerChannel.getId());
            }
            sdkCustomerChannelMapper.batchUpsertSdkCustomerChannelConfig(osConfigs);
        }

        return getOneSdkCustomerChannel(sdkCustomerChannel.getId());
    }
}
