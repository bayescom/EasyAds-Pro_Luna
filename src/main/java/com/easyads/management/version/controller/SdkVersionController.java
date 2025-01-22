package com.easyads.management.version.controller;


import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.version.model.bean.Sdkver;
import com.easyads.management.version.service.SdkVersionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/sdkver")
public class SdkVersionController {

    @Autowired
    private SdkVersionService sdkVersionService;

    @GetMapping("/list")
    public Map<String, Object> getSdkverList(@RequestParam Map<String, Object> requestParam,
                                             HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkVersionService.getSdkverList(requestParam);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOneSdkver(@PathVariable Long id,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkVersionService.getOneSdkver(id);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @PostMapping("/")
    public Map<String, Object> addOneSdkver(@RequestBody String requestBody,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            Sdkver sdkver = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdkver"), Sdkver.class);
            return sdkVersionService.addOneSdkver(sdkver);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateOneSdkver(@PathVariable Long id, @RequestBody String requestBody,
                                               HttpServletRequest request, HttpServletResponse response) {
        try {
            Sdkver sdkver = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdkver"), Sdkver.class);
            return sdkVersionService.updateOneSdkver(id, sdkver);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}