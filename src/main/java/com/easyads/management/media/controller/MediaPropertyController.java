package com.easyads.management.media.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.management.media.service.MediaPropertyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value="/media")
public class MediaPropertyController {
    @Autowired
    private MediaPropertyService mediaPropertyService;

    @GetMapping("/platform")
    public Object getMediaPlatform(HttpServletRequest request, HttpServletResponse response) {
        try {
            return mediaPropertyService.getPlatformList();
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
