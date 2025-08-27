package com.easyads.management.experiment.exp.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.management.experiment.exp.service.SdkExperimentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/sdk_experiment")
public class SdkExperimentController {

    @Autowired
    private SdkExperimentService sdkExperimentService;

    @GetMapping("/list")
    public Map<String, Object> getSdkExperimentList(@RequestParam Map<String, Object> queryParams,
                                                     HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkExperimentService.getSdkExperimentList(queryParams);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e.toString());
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOneSdkExperiment(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkExperimentService.getOneSdkExperiment(id);
        } catch (BadRequestException e) {
            response.setStatus(400);
            request.setAttribute("message", e.getMessage());
            log.warn("params error req params = {}", request.getParameterMap().toString());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e.toString());
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

}
