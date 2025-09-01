package com.easyads.management.experiment.report.controller;

import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.management.experiment.report.service.SdkExperimentReportService;
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
@RequestMapping(value="/sdk_experiment")
public class SdkExperimentReportController {
    
    @Autowired
    private SdkExperimentReportService sdkExperimentReportService;

    @GetMapping("/report")
    public Map<String, Object> getSdkExperimentReport(@RequestParam Map<String, Object> queryParams,
                                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            return sdkExperimentReportService.getSdkExperimentReport(queryParams);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e.toString());
        }
        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }

    @GetMapping(value = "/download", produces = "application/octet-stream")
    public void SdkExperimentReportDownload(@RequestParam Map<String, Object> queryParams,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            sdkExperimentReportService.sdkExperimentReportDownload(queryParams, response);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e.toString());
        }
    }
}
