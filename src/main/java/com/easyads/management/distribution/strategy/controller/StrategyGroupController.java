package com.easyads.management.distribution.strategy.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.strategy.model.group.SdkGroupStrategy;
import com.easyads.management.distribution.strategy.service.StrategyGroupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/adspot/sdk/group_strategy")
// 流量分组管理
public class StrategyGroupController {

    @Autowired
    private StrategyGroupService adspotSdkGroupService;

    @GetMapping("/{adspotId}/{percentageId}")
    public Object getOnePercentageStrategyGroup(@PathVariable Integer adspotId,
                                                @PathVariable Integer percentageId,
                                                HttpServletRequest request, HttpServletResponse response) {
        try {
            return adspotSdkGroupService.getOnePercentageGroupStrategy(adspotId, percentageId);
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

    @PutMapping("/{adspotId}/{percentageId}")
    public Object updateOnePercentageStrategyGroup(@PathVariable Integer adspotId,
                                                   @PathVariable Integer percentageId,
                                                   @RequestBody String requestBody,
                                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            List<SdkGroupStrategy> sdkGroupStrategyList = JsonUtils.convertJsonNodeToList(JsonUtils.getJsonNode(requestBody).get("groupStrategyList"), SdkGroupStrategy.class);
            return adspotSdkGroupService.updateOnePercentageGroupStrategy(adspotId, percentageId, sdkGroupStrategyList);
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
