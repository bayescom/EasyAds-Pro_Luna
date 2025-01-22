package com.easyads.management.dimension.controller;


import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.management.dimension.service.DimensionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/dimension")
public class DimensionController {

    @Autowired
    private DimensionService directionService;


    @GetMapping("/location")
    public Map<String, Object> getLocationInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            return directionService.getLocationInfo();
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/make")
    public Map<String, Object> getMakeInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            return directionService.getMakeInfo();
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/osv")
    public Map<String, Object> getOsvInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            return directionService.getOsvInfo();
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/appver")
    public Map<String, Object> getAppverInfo(@RequestParam String mediaId,
                                             HttpServletRequest request, HttpServletResponse response) {
        try {
            return directionService.getAppverInfo(mediaId);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/sdkver")
    public Map<String, Object> getSdkverInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            return directionService.getSdkverInfo();
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}