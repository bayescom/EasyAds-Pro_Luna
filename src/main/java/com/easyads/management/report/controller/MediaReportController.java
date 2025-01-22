package com.easyads.management.report.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.management.report.model.bean.data.filter.MediaReportFilterParams;
import com.easyads.management.report.service.MediaReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/traffic/report")
public class MediaReportController {
    @Autowired
    private MediaReportService mediaReportService;

    @GetMapping("/chart")
    public Object getMediaReportChart(@RequestParam Map<String, Object> queryParams,
                                      HttpServletRequest request, HttpServletResponse response) {
        try {
            Pair<MediaReportFilterParams, MediaReportFilterParams> filterParams = mediaReportService.genMediaReportFilterWithContrast(queryParams);
            return mediaReportService.getMediaReportChart(filterParams);
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

    @GetMapping("/detail")
    public Object getMediaReportDetail(@RequestParam Map<String, Object> queryParams, HttpServletRequest request, HttpServletResponse response) {
        try {
            Pair<MediaReportFilterParams, MediaReportFilterParams> filterParams = mediaReportService.genMediaReportFilterWithContrast(queryParams);
            return mediaReportService.getMediaReportDetail(filterParams);
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

    @GetMapping(value = "/download", produces = "application/octet-stream")
    public void mediaReportDownload(@RequestParam Map<String, Object> queryParams,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            queryParams.put("isDownload", true); // 增加这样一个参数，为了方便区分是不是报表下载
            mediaReportService.mediaReportDownload(queryParams, response);
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }
    }
}
