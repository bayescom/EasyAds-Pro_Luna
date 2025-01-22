package com.easyads.management.distribution.sdk.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.easyads.management.distribution.sdk.service.AdspotSdkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/adspot/sdk")
public class AdspotSdkController {

    @Autowired
    private AdspotSdkService adspotSdkService;

    @GetMapping("/{adspotId}")
    public Object getOneAdspotSdkChannelList(@PathVariable Long adspotId,
                                             @RequestParam Map<String, Object> queryParams,
                                             HttpServletRequest request, HttpServletResponse response) {

        try {
            return adspotSdkService.getOneAdspotSdkChannelList(queryParams, adspotId);
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

    @PostMapping("/{adspotId}")
    public Object createOneAdspotSdkChannel(@PathVariable Long adspotId,
                                            @RequestBody String requestBody,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            SdkChannel sdkChannel = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_channel"), SdkChannel.class);
            response.setStatus(201);
            return adspotSdkService.createOneAdspotSdkChannel(adspotId, sdkChannel);
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

    @PostMapping("/{adspotId}/batch")
    public Object createMultiAdspotSdkChannel(@PathVariable Long adspotId,
                                              @RequestBody String requestBody,
                                              HttpServletRequest request, HttpServletResponse response) {
        try {
            List<SdkChannel> sdkChannelList = JsonUtils.convertJsonNodeToList(JsonUtils.getJsonNode(requestBody).get("sdkChannelList"), SdkChannel.class);
            response.setStatus(201);
            return adspotSdkService.createMultiAdspotSdkChannel(adspotId, sdkChannelList);
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

    @GetMapping("/{adspotId}/{sdkChannelId}/status")
    public Object getOneAdspotSdkChannelUsingStatus(@PathVariable Integer adspotId,
                                                    @PathVariable Integer sdkChannelId,
                                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotSdkService.getOneAdspotSdkChannelUsingStatus(adspotId, sdkChannelId);
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

    @GetMapping("/{adspotId}/{sdkChannelId}")
    public Object getOneAdspotOneSdkChannel(@PathVariable Long adspotId,
                                            @PathVariable Integer sdkChannelId,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotSdkService.getOneAdspotOneSdkChannel(adspotId, sdkChannelId);
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


    @PutMapping("/{adspotId}/{sdkChannelId}")
    public Object updateOneAdspotOneSdkChannel(@PathVariable Long adspotId,
                                               @PathVariable Integer sdkChannelId,
                                               @RequestBody String requestBody,
                                               HttpServletRequest request, HttpServletResponse response) {
        try {
            SdkChannel sdkChannel = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_channel"), SdkChannel.class);
            return adspotSdkService.updateOneAdspotOneSdkChannel(adspotId, sdkChannelId, sdkChannel);
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

    @DeleteMapping("/{adspotId}/{sdkChannelId}")
    public Object deleteOneAdspotOneSdkChannel(@PathVariable Long adspotId,
                                               @PathVariable Integer sdkChannelId,
                                               HttpServletRequest request, HttpServletResponse response) {
        try {
            adspotSdkService.deleteOneAdspotOneSdkChannel(adspotId, sdkChannelId);
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
