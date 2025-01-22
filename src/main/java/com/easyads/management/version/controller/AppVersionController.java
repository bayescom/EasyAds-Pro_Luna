package com.easyads.management.version.controller;

import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.version.model.bean.Appver;
import com.easyads.management.version.service.AppVersionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/appver")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    @GetMapping("/list")
    public Map<String, Object> getAppverList(@RequestParam Map<String, Object> requestParam,
                                             HttpServletRequest request, HttpServletResponse response) {
        try {
            return appVersionService.getAppverList(requestParam);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOneAppver(@PathVariable Long id,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            return appVersionService.getOneAppver(id);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @PostMapping("/")
    public Map<String, Object> addOneAppver(@RequestBody String requestBody,
                                            HttpServletRequest request, HttpServletResponse response) {
        try {
            Appver appver = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("appver"), Appver.class);
            return appVersionService.addOneAppver(appver);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateOneAppver(@PathVariable Long id, @RequestBody String requestBody,
                                               HttpServletRequest request, HttpServletResponse response) {
        try {
            Appver appver = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("appver"), Appver.class);
            return appVersionService.updateOneAppver(id, appver);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}