package com.easyads.management.adn.service;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.MediaReportMapper;
import com.easyads.component.mapper.SdkAdnMapper;
import com.easyads.component.mapper.SdkCustomerChannelMapper;
import com.easyads.management.adn.model.bean.SdkAdn;
import com.easyads.management.adn.model.bean.SdkAdnReportApi;
import com.easyads.management.adn.model.data.ChannelDataFilter;
import com.easyads.management.adn.model.filter.SdkAdnFilterParams;
import com.easyads.management.adn.model.data.SdkData;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannel;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelConfig;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelFilterParams;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelMeta;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SdkAdnService {

    @Autowired
    private SdkAdnMapper sdkAdnMapper;

    @Autowired
    private MediaReportMapper mediaReportMapper;

    @Autowired
    private SdkCustomerChannelMapper sdkCustomerChannelMapper;

    /**
     * 判断自定义SDK是否应该被过滤掉
     * @param customSdk 自定义SDK
     * @param platformType 操作系统类型 (0-iOS, 1-Android, 2-鸿蒙)
     * @param adspotType 广告位类型
     * @param renderType 渲染类型
     * @return true-过滤掉(不展示)，false-保留(展示)
     */
    private boolean isCustomSdkFiltered(SdkCustomerChannel customSdk, Integer platformType, Integer adspotType, Integer renderType) {
        // 没有传入任何过滤条件时，不过滤（返回 false 表示保留）
        if (platformType == null && adspotType == null && renderType == null) {
            return false;
        }

        if (customSdk == null) {
            return true;
        }

        List<SdkCustomerChannelConfig> configs = customSdk.getOsConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            return true;
        }

        List<SdkCustomerChannelConfig> candidates = new ArrayList<>();
        for (SdkCustomerChannelConfig config : configs) {
            if (platformType != null && !Objects.equals(config.getOsType(), platformType)) {
                continue;
            }
            if (!Integer.valueOf(1).equals(config.getStatus())) {
                continue;
            }
            candidates.add(config);
        }

        if (candidates.isEmpty()) {
            return true;
        }

        if (adspotType == null) {
            return false;
        }

        String adapterKey = getAdapterKeyByAdspotType(adspotType, renderType);
        if (adapterKey == null) {
            return true;
        }

        for (SdkCustomerChannelConfig config : candidates) {
            Object configObj = config.getConfigObj();
            if (!(configObj instanceof Map)) {
                continue;
            }
            Map<?, ?> configMap = (Map<?, ?>) configObj;
            Object adapterValue = configMap.get(adapterKey);
            if (adapterValue != null && StringUtils.isNotBlank(String.valueOf(adapterValue))) {
                return false;
            }
        }

        return true;
    }

    private String getAdapterKeyByAdspotType(Integer adspotType, Integer renderType) {
        if (adspotType == null) {
            return null;
        }

        switch (adspotType) {
            case 1:
                return "coopen";
            case 2:
                if (renderType != null && renderType == 0) {
                    return "custom_feeds";
                } else if (renderType != null && renderType == 1) {
                    return "template_feeds";
                }
                return null;

            case 4:
                return "interstitial";
            case 5:
                return "reward";
            default:
                return null;
        }
    }

    /**
     * 将自定义 SDK 广告网络转换为 SdkAdn 格式
     */
    private SdkAdn convertToSdkAdn(SdkCustomerChannelMeta customSdk) {
        SdkAdn summary = new SdkAdn();

        summary.setAdnId(customSdk.getId());
        summary.setAdnName(customSdk.getName());
        summary.setStatus(1);
        summary.setSupportAutoCreate(0);
        summary.setIsCustom(1);
        summary.setReportApiStatus(0);
        summary.setAdnParamsMeta(customSdk.toAdnParamsMeta());
        summary.setReportApiParams(new ArrayList<>());
        summary.setReportApiParamsMeta(new ArrayList<>());
        summary.setData(new SdkData());

        return summary;
    }

    private int appendCustomSdkList(List<SdkAdn> sdkAdnList, SdkAdnFilterParams filterParams,
                                    Integer adspotType, Integer platformType, Integer renderType) {
        // 自定义渠道当前固定 status=1，筛选未启用时不返回
        if (filterParams.status != null && filterParams.status == 0) {
            return 0;
        }

        List<SdkCustomerChannelMeta> customSdkList = sdkCustomerChannelMapper.getSdkCustomerChannelMetaList();
        if (CollectionUtils.isEmpty(customSdkList)) {
            return 0;
        }

        List<SdkCustomerChannel> customSdkWithConfigList =
                sdkCustomerChannelMapper.getSdkCustomerChannelList(new SdkCustomerChannelFilterParams(new HashMap<>()));
        Map<Integer, SdkCustomerChannel> customSdkConfigMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(customSdkWithConfigList)) {
            for (SdkCustomerChannel channel : customSdkWithConfigList) {
                customSdkConfigMap.put(channel.getId(), channel);
            }
        }

        int customSdkCount = 0;
        for (SdkCustomerChannelMeta customSdk : customSdkList) {
            if (StringUtils.isNotBlank(filterParams.searchText)
                    && (StringUtils.isBlank(customSdk.getName()) || !customSdk.getName().contains(filterParams.searchText))) {
                continue;
            }

            SdkCustomerChannel customSdkDetail = customSdkConfigMap.get(customSdk.getId());
            if (isCustomSdkFiltered(customSdkDetail, platformType, adspotType, renderType)) {
                continue;
            }

            sdkAdnList.add(convertToSdkAdn(customSdk));
            customSdkCount++;
        }
        return customSdkCount;
    }

    public Map<String, Object> getSdkAdnlList(Map<String, Object> queryParams, Integer adspotType, Integer platformType, Integer renderType) throws BadRequestException {
        Map<String, Object> channelResult = new HashMap(){{
            put("meta", new HashMap(){{put("total", 0);}});
            put("sdk_adns", new ArrayList<>());
        }};

        SdkAdnFilterParams filterParams = new SdkAdnFilterParams(queryParams);
        int sdkChannelCount = sdkAdnMapper.getSdkAdnCount(filterParams);
        List<SdkAdn> sdkAdnList = sdkAdnMapper.getSdkAdnList(filterParams);

        for(SdkAdn sdkAdn : sdkAdnList) {
            sdkAdn.setData(new SdkData());
        }

        // 获取渠道的流量数据
        ChannelDataFilter dataFilter = new ChannelDataFilter(null, filterParams.beginTime, filterParams.endTime);
        Map<String, SdkData> sdkChannelData = mediaReportMapper.getSdkChannelTrafficData(dataFilter);

        for(SdkAdn sdkAdn : sdkAdnList) {
            SdkData sdkData = sdkChannelData.get(sdkAdn.getAdnId().toString());
            if(null != sdkData) {
                sdkData.completeIndicator();
                sdkAdn.setData(sdkData);
            } else {
                sdkAdn.setData(new SdkData());
            }
        }

        // 查询自定义 SDK 广告网络列表并拼接到结果中（流量数据暂不填充）
        int customSdkCount = appendCustomSdkList(sdkAdnList, filterParams, adspotType, platformType, renderType);

        ((Map) channelResult.get("meta")).put("total", sdkChannelCount + customSdkCount);
        channelResult.put("sdk_adns", sdkAdnList);

        return channelResult;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager = "easyadsDbTransactionManager")
    public Map<String, Object> updateSdkAdn(Long sdkAdnId, SdkAdn sdkAdn) throws BadRequestException {
        Map<String, Object> channelResult = new HashMap();

        // 获取当前渠道目前已经存在的companyChannelId集合
        Set<Integer> sdkChannelReportApiIdSet = sdkAdnMapper.getSdkAdnReportApiIdList(sdkAdnId);

        // 对渠道参数里面的meta信息进行转化，获得meta_key -> meta_id的映射关系，后面会使用到
        for(SdkAdnReportApi sari : sdkAdn.getReportApiParams()) {
            // 转化并补全ReportApi参数相关信息
            sari.completeDbBean();

            if(null == sari.getId()) {
                // 如果reportApiId为空，则是新增一个参数
                sdkAdnMapper.createSdkAdnOneReportApi(sdkAdnId, sari);
            } else if(sdkChannelReportApiIdSet.contains(sari.getId())) {
                // 如果是一个已经存在的则是更新该参数
                sdkAdnMapper.updateSdkAdnOneReportApi(sari);
                sdkChannelReportApiIdSet.remove(sari.getId());
            }
        }

        // 删除掉已经不存在的渠道
        if(CollectionUtils.isNotEmpty(sdkChannelReportApiIdSet)) {
            for(Integer sdkAdnReportApiId : sdkChannelReportApiIdSet) {
                sdkAdnMapper.deleteSdkAdnOneReportApi(sdkAdnId, sdkAdnReportApiId);
            }
        }

        channelResult.put("sdk_Adn", sdkAdnMapper.getOneSdkAdnReportApi(sdkAdnId));
        return channelResult;
    }
}
