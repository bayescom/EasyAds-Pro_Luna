package com.easyads.management.sdk_customer_channel.controller;

import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannel;
import com.easyads.management.sdk_customer_channel.service.SdkCustomerChannelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/sdk_customer_channel")
public class SdkCustomerChannelController {

    @Autowired
    private SdkCustomerChannelService sdkCustomerChannelService;

    @GetMapping("/list")
    public Object getSdkCustomerChannelList(@RequestParam Map<String, Object> queryParams,
                                          HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkCustomerChannelService.getSdkCustomerChannelList(queryParams);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }


    @PostMapping("/")
    public Object createOneSdkCustomerChannel(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
        try {
            SdkCustomerChannel sdkCustomerChannel = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_customer_channel"), SdkCustomerChannel.class);
            response.setStatus(201);
            return sdkCustomerChannelService.createOneSdkCustomerChannel(sdkCustomerChannel);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/{id}")
    public Object getOneSdkCustomerChannel(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(201);
            return sdkCustomerChannelService.getOneSdkCustomerChannel(id);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/adapter_adspot_type")
    public Map<String, Object> getAdspotRenderType(HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkCustomerChannelService.getAdapterAdspotType();
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }


    @PutMapping("/{id}")
    public Object updateOneSdkCustomerChannel(@PathVariable Long id, @RequestBody String requestBody,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            SdkCustomerChannel sdkCustomerChannel = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_customer_channel"), SdkCustomerChannel.class);
            return sdkCustomerChannelService.updateOneSdkCustomerChannel(id, sdkCustomerChannel);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}
