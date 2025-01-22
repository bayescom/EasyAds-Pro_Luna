package com.easyads.management.report.controller;

import com.easyads.component.enums.FilterEnum;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.management.report.model.filter.FilterParams;
import com.easyads.management.report.model.filter.TrafficDataFilterParams;
import com.easyads.management.report.service.TrafficReportFilterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/traffic/filter")
public class MediaReportFilterController {
    @Autowired
    private TrafficReportFilterService trafficReportFilterService;

    @GetMapping("/media")
    public Object getTrafficMedia(@RequestParam Map<String, Object> queryParams,
                                  HttpServletRequest request, HttpServletResponse response) {

        try {
            Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair = trafficReportFilterService.genReportDataFilterParamsWithContrast(queryParams, FilterEnum.MEDIA);
            FilterParams filterParams = new FilterParams(queryParams);
            return trafficReportFilterService.getTrafficMedia(dataFilterParamsPair, filterParams);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }

        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/adspot")
    public Object getTrafficAdspot(@RequestParam Map<String, Object> queryParams,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair = trafficReportFilterService.genReportDataFilterParamsWithContrast(queryParams, FilterEnum.ADSPOT);
            FilterParams filterParams = new FilterParams(queryParams);
            return trafficReportFilterService.getTrafficAdspot(dataFilterParamsPair, filterParams);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/channel")
    public Object getTrafficChannel(@RequestParam Map<String, Object> queryParams,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair = trafficReportFilterService.genReportDataFilterParamsWithContrast(queryParams, FilterEnum.CHANNEL);
            FilterParams filterParams = new FilterParams(queryParams);
            return trafficReportFilterService.getTrafficChannel(dataFilterParamsPair, filterParams);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/adspot_meta")
    public Object getTrafficAdspotMeta(@RequestParam Map<String, Object> queryParams,
                                       HttpServletRequest request, HttpServletResponse response) {

        try {
            Pair<TrafficDataFilterParams, TrafficDataFilterParams> dataFilterParamsPair = trafficReportFilterService.genReportDataFilterParamsWithContrast(queryParams, FilterEnum.META_ADSPOT);
            FilterParams filterParams = new FilterParams(queryParams);
            return trafficReportFilterService.getTrafficMetaAdspot(dataFilterParamsPair, filterParams);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}
