package com.easyads.management.distribution.sdk.service;

import com.easyads.component.mapper.SdkAdnMapper;
import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.adn.model.bean.SdkAdnReportApi;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutoAdspotService {

    @Autowired
    private SdkChannelMapper sdkChannelMapper;

    @Autowired
    private SdkAdnMapper sdkAdnMapper;

    @Autowired
    private CsjAutoAdspotService csjAutoAdspotService;

    @Autowired
    private YlhAutoAdspotService ylhAutoAdspotService;
    
    @Autowired
    private BdAutoAdspotService bdAutoAdspotService;

    @Autowired
    private KsAutoAdspotService ksAutoAdspotService;

    private final ObjectMapper mapper = new ObjectMapper();

    // 只有getOne 和 更新，编辑才需要单独出来
    public Map<String, Object> getOneAdspotSdkChannel(Long adspotId, Integer sdkChannelId, String source, Integer adspotType) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        SdkChannel sdkChannel = sdkChannelMapper.getOneAdspotSdkChannel(adspotId, sdkChannelId);

        resultMap.put("sdkChannel", sdkChannel);

        JsonNode autoAdspot = mapper.createObjectNode();

        if ("csj".equals(source)) {
            autoAdspot = csjAutoAdspotService.getOneCsjAdspotSdkChannel(sdkChannel);
        } else if ("bd".equals(source)) {
            autoAdspot = bdAutoAdspotService.getOneBdAdspotSdkChannel(sdkChannel, sdkChannelId, adspotId);
        } else if ("ks".equals(source)) {
            autoAdspot = ksAutoAdspotService.getOneKsAdspotSdkChannel(sdkChannel, sdkChannelId, adspotId, adspotType);
        } else if ("ylh".equals(source)) {
            autoAdspot = ylhAutoAdspotService.getOneYlhAdspotSdkChannel(sdkChannel);
        }

        resultMap.put("auto_adspot", autoAdspot);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> createOneAdspotSdkChannel(Long adspotId, Integer adspotType, String source, String requestBody) throws Exception {
        // 将更新数据中的字段改变成数据库可写字段
        SdkChannel sdkChannel = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_channel"), SdkChannel.class);
        sdkChannel.setIsAutoCreate(1);
        sdkChannel.completeDbBean();

        if ("csj".equals(source)) {
            csjAutoAdspotService.createOneCsjAdspotSdkChannel(sdkChannel, adspotType, requestBody);
        } else if ("bd".equals(source)) {
            bdAutoAdspotService.createOneBdAdspotSdkChannel(sdkChannel, adspotType, requestBody);
        } else if ("ks".equals(source)) {
            ksAutoAdspotService.createOneKsAdspotSdkChannel(sdkChannel, adspotType, requestBody);
        } else if ("ylh".equals(source)) {
            ylhAutoAdspotService.createOneYlhAdspotSdkChannel(sdkChannel, adspotType, requestBody);
        }

        // 创建SDK渠道的Report API
        SdkAdnReportApi reportApiParam = sdkChannel.getReportApiParam();
        if(null != reportApiParam) {
            if(reportApiParam.needCreate()) {
                // id为null，并且channel_params不为空就需要重新创建一个新的
                reportApiParam.completeDbBean();
                sdkAdnMapper.createSdkAdnOneReportApi(sdkChannel.getAdnId(), reportApiParam);
            }
        }

        // 创建渠道
        sdkChannelMapper.createOneAdspotSdkChannel(adspotId, sdkChannel);

        return getOneAdspotSdkChannel(adspotId, sdkChannel.getId(), source, adspotType);
    }


    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneAdspotSdkChannel(Long adspotId, Integer sdkChannelId, String source, String requestBody, int adspotType) throws Exception {
        SdkChannel sdkChannel = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_channel"), SdkChannel.class);

        sdkChannel.completeDbBean();

        SdkChannel originSdkChannel = sdkChannelMapper.getOneAdspotSdkChannel(adspotId, sdkChannelId);

        // 主要是改这里，根据不同的类型，使用不同的service
        if ("csj".equals(source)) {
            csjAutoAdspotService.updateOneCsjAdspotSdkChannel(sdkChannel, originSdkChannel, requestBody, adspotType);
        } else if ("bd".equals(source)) {
            bdAutoAdspotService.updateOneBdAdspotSdkChannel(sdkChannel, originSdkChannel, requestBody, adspotType);
        } else if ("ks".equals(source)) {
            ksAutoAdspotService.updateOneKsAdspotSdkChannel(sdkChannel, originSdkChannel, requestBody, adspotType);
        } else if ("ylh".equals(source)) {
            ylhAutoAdspotService.updateOneYlhAdspotSdkChannel(sdkChannel, originSdkChannel, requestBody, adspotType);
        }

        // 创建SDK渠道的Report API
        SdkAdnReportApi reportApiParam = sdkChannel.getReportApiParam();
        if(null != reportApiParam) {
            if(reportApiParam.needCreate()) {
                // id为null，并且channel_params不为空就需要重新创建一个新的
                reportApiParam.completeDbBean();
                sdkAdnMapper.createSdkAdnOneReportApi(sdkChannel.getAdnId(), reportApiParam);
            }
        }

        sdkChannelMapper.updateOneAdspotSdkChannel(adspotId, sdkChannelId, sdkChannel);
        return getOneAdspotSdkChannel(adspotId, sdkChannel.getId(), source, adspotType);
    }

}
