package com.easyads.management.distribution.sdk.controller;

import com.easyads.component.exception.AutoAdspotException;
import com.easyads.component.exception.BadRequestException;
import com.easyads.management.distribution.sdk.service.AutoAdspotService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.easyads.component.rpc.ResponseCodeUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value="/adspot/sdk/auto_adspot")
public class AutoAdspotController {
    @Autowired
    private AutoAdspotService autoAdspotService;

    @GetMapping("/{adspotId}/{sdkChannelId}/{adspotType}/{source}")
    public Object getOneAdspotSdkChannel(@PathVariable Long adspotId,
                                         @PathVariable Integer sdkChannelId,
                                         @PathVariable Integer adspotType,
                                         @PathVariable String source,
                                         HttpServletRequest request, HttpServletResponse response) {
        try {
            return autoAdspotService.getOneAdspotSdkChannel(adspotId, sdkChannelId, source, adspotType);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (AutoAdspotException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("三方创建广告位参数错误", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }


    @PostMapping("/{adspotId}/{adspotType}/{source}")
    public Object createOneAdspotSdkChannel(@PathVariable Long adspotId,
                                            @PathVariable int adspotType,
                                            @PathVariable String source,
                                            @RequestBody String requestBody,
                                            HttpServletRequest request, HttpServletResponse response) {


        try {
            response.setStatus(201);
            return autoAdspotService.createOneAdspotSdkChannel(adspotId, adspotType, source, requestBody);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (AutoAdspotException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("三方创建广告位参数错误", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", e.getMessage());
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @PutMapping("/{adspotId}/{sdkChannelId}/{adspotType}/{source}")
    public Object updateOneAdspotSdkChannel(@PathVariable Long adspotId,
                                            @PathVariable int adspotType,
                                            @PathVariable Integer sdkChannelId,
                                            @PathVariable String source,
                                            @RequestBody String requestBody,
                                            HttpServletRequest request, HttpServletResponse response) {

        try {
            return autoAdspotService.updateOneAdspotSdkChannel(adspotId, sdkChannelId, source, requestBody, adspotType);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (AutoAdspotException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("三方创建广告位参数错误", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", e.getMessage());
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

}
