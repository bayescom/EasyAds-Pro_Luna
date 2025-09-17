package com.easyads.management.distribution.strategy.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.strategy.model.target_percentage.SdkTargetPercentageExperiment;
import com.easyads.management.distribution.strategy.service.StrategyTargetPercentageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value="/adspot/sdk")
// AB测试-瀑布流
public class StrategyTargetPercentageController {

    @Autowired
    private StrategyTargetPercentageService adspotSdkTargetPercentageService;

    @GetMapping("/strategy_direction/{targetId}")
    public Object getTrafficOneTargetStrategy(@PathVariable Long targetId,
                                              HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotSdkTargetPercentageService.getOneTargetStrategy(targetId);
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

    @PutMapping("/traffic_percentage/{adspotId}/{percentageId}/{targetId}")
    public Object updateTrafficTargetPercentage(@PathVariable Integer adspotId,
                                                @PathVariable Long percentageId,
                                                @PathVariable Long targetId,
                                                @RequestBody String requestBody,
                                                HttpServletRequest request, HttpServletResponse response) {

        try {
            SdkTargetPercentageExperiment sdkTargetPercentageExperiment = JsonUtils.convertJsonToObject(requestBody, SdkTargetPercentageExperiment.class);
            return adspotSdkTargetPercentageService.updateTargetPercentage(adspotId, percentageId, targetId, sdkTargetPercentageExperiment);
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
