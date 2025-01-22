package com.easyads.management.adn.service;

import com.easyads.component.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdnReportApiService {
//    @Autowired
//    private ChannelReportApiMapper channelReportApiMapper;

    public Map<String, Object> getChannelReportApi(Long sdkChannelId) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();

//        ReportApiParamSetting reportApiParamSetting = channelReportApiMapper.getChannelReportApi(sdkChannelId);
//
//        resultMap.put("report_api", reportApiParamSetting);

        return resultMap;
    }
}
