package com.easyads.management.distribution.strategy.controller;


import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.strategy.model.percentage.SdkPercentage;
import com.easyads.management.distribution.strategy.model.percentage.SdkTrafficPercentageExperiment;
import com.easyads.management.distribution.strategy.service.StrategyPercentageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/adspot/sdk")
// AB测试-流量分组
public class StrategyPercentageController {

    @Autowired
    private StrategyPercentageService adspotSdkPercentageService;

    @GetMapping("/traffic_percentage/{adspotId}")
    public Object getTrafficPercentage(@PathVariable Integer adspotId,
                                       HttpServletRequest request, HttpServletResponse response) {

        try {
            return adspotSdkPercentageService.getOneTrafficPercentageList(adspotId);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @PutMapping("/traffic_percentage/{adspotId}")
    public Object updateTrafficPercentage(@PathVariable Integer adspotId,
                                          @RequestBody String requestBody,
                                          HttpServletRequest request, HttpServletResponse response) {
        try {
            SdkTrafficPercentageExperiment sdkTrafficPercentageExperiment = JsonUtils.convertJsonToObject(requestBody, SdkTrafficPercentageExperiment.class);
            return adspotSdkPercentageService.updateTrafficPercentage(adspotId, sdkTrafficPercentageExperiment);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}
