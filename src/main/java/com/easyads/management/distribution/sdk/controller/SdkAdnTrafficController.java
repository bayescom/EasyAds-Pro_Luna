package com.easyads.management.distribution.sdk.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.easyads.management.distribution.sdk.service.AdspotSdkService;
import com.easyads.management.distribution.sdk.service.SdkAdnTrafficService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/sdk_channel/traffic")
public class SdkAdnTrafficController {

    @Autowired
    private SdkAdnTrafficService sdkAdnTrafficService;

    @GetMapping("/{sdkChannelId}")
    public Object getOneSdkChannelTraffic(@PathVariable Long sdkChannelId, @RequestParam Map<String, Object> queryParams,
                                          HttpServletRequest request, HttpServletResponse response) {

        try {
            return sdkAdnTrafficService.getOneSdkChannelTraffic(sdkChannelId, queryParams);
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

    @PutMapping("/{sdkGroupId}/{adspotSdkChannelId}")
    public Object updateOneSdkChannelTrafficStatus(@PathVariable Long sdkGroupId,
                                                   @PathVariable Long adspotSdkChannelId,
                                                   @RequestParam Map<String, Object> queryParams,
                                                   HttpServletRequest request, HttpServletResponse response) {

        try {
            return sdkAdnTrafficService.updateOneSdkChannelTrafficStatus(sdkGroupId, adspotSdkChannelId, queryParams);
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
