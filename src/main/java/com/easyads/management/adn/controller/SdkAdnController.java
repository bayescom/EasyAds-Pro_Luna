package com.easyads.management.adn.controller;


import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.adn.model.bean.SdkAdn;
import com.easyads.management.adn.service.SdkAdnService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/sdk_adn")
public class SdkAdnController {

    @Autowired
    private SdkAdnService adnChannelService;

    @GetMapping("/adns")
    public Object getAdnList(@RequestParam Map<String, Object> queryParams,
                             HttpServletRequest request, HttpServletResponse response) {
        try {
            return adnChannelService.getSdkAdnlList(queryParams);
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

    @PutMapping("/{sdkAdnId}")
    public Object updateOneAdn(@PathVariable Long sdkAdnId,
                               @RequestBody String requestBody,
                               HttpServletRequest request, HttpServletResponse response) {
        try {
            SdkAdn sdkAdn = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("sdk_adn"), SdkAdn.class);
            return adnChannelService.updateSdkAdn(sdkAdnId, sdkAdn);
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
