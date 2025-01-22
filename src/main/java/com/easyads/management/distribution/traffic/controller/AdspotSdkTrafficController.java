package com.easyads.management.distribution.traffic.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.traffic.service.AdspotSdkTrafficService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/adspot/sdk_traffic")
public class AdspotSdkTrafficController {

    @Autowired
    private AdspotSdkTrafficService adspotSdkTrafficService;

    @GetMapping("/{adspotId}")
    public Object getOneAdspotSdkAllTraffic(@PathVariable Integer adspotId,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotSdkTrafficService.getOneAdspotSdkAllTraffic(adspotId);
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


    @PutMapping("/{adspotId}/{sdkTrafficId}")
    public Object updateOneAdspotOneSdkTraffic(@PathVariable Long adspotId,
                                               @PathVariable Long sdkTrafficId,
                                               @RequestBody String requestBody,
                                               HttpServletRequest request, HttpServletResponse response) {

        try {
            Map<String, String> sdkTrafficMap = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdkSupplierMap"), Map.class);
            return adspotSdkTrafficService.updateOneSdkTraffic(adspotId, sdkTrafficId, sdkTrafficMap);
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
