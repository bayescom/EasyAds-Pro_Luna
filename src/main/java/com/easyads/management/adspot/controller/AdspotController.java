package com.easyads.management.adspot.controller;


import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.adspot.model.Adspot;
import com.easyads.management.adspot.service.AdspotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/adspot")
public class AdspotController {

    @Autowired
    private AdspotService adspotService;

    @GetMapping("adspots")
    public Map<String, Object> getAdspotList(@RequestParam Map<String, Object> queryParams,
                                             HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotService.getAdspotList(queryParams);
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

    @PostMapping("/")
    public Object createOneAdspot(@RequestBody String requestBody,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            Adspot adspot = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("adspot"), Adspot.class);
            response.setStatus(201);
            return adspotService.createOneAdspot(adspot);
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

    @GetMapping("/{adspotId}")
    public Object getOneAdspot(@PathVariable Long adspotId,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotService.getOneAdspot(adspotId);
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

    @PutMapping("/{adspotId}")
    public Object updateOneAdspot(@PathVariable Long adspotId,
                                  @RequestBody String requestBody,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            Adspot adspot = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("adspot"), Adspot.class);
            return adspotService.updateOneAdspot(adspotId, adspot);
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

    @DeleteMapping("/{adspotId}")
    public Object deleteOneAdspot(@PathVariable Long adspotId,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(204);
            return adspotService.deleteOneAdspot(adspotId);
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

    // 批量操作接口
    /**
     * 仅更新广告位状态status
     *
     * @param requestBody
     * @param request
     * @param response
     * @return
     */
    @PutMapping("/status")
    public Object updateMultiAdspotStatus(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Adspot> adspotList = JsonUtils.convertJsonNodeToList(JsonUtils.getJsonNode(requestBody).get("adspots"), Adspot.class);
            return adspotService.updateMultiAdspotStatus(adspotList);
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
