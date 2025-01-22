package com.easyads.management.media.controller;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.media.model.Media;
import com.easyads.management.media.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/media")
public class MediaController {
    @Autowired
    private MediaService mediaService;

    @GetMapping("/medias")
    public Object getMediaList(@RequestParam Map<String, Object> queryParams,
                               HttpServletRequest request, HttpServletResponse response) {

        try {
            return mediaService.getMediaList(queryParams);
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

    @GetMapping("/{mediaId}")
    public Object getOneMedia(@PathVariable Long mediaId,
                              HttpServletRequest request, HttpServletResponse response) {
        try {
            return mediaService.getOneMedia(mediaId);
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
    public Object createOneMedia(@RequestBody String requestBody,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            Media media = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("media"), Media.class);
            response.setStatus(201);
            return mediaService.createOneMedia(media);
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


    @PutMapping("/{mediaId}")
    public Object updateOneMedia(@PathVariable Long mediaId, @RequestBody String requestBody,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            Media media = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("media"), Media.class);
            return mediaService.updateOneMedia(mediaId, media);
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

    @DeleteMapping("/{mediaId}")
    public Object deleteOneMedia(@PathVariable Long mediaId,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(204);
            return mediaService.deleteOneMedia(mediaId);
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
    @PutMapping("/status")
    public Object updateMultiMediaStatus(@RequestBody String requestBody,
                                         HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Media> mediaList = JsonUtils.convertJsonNodeToList(JsonUtils.getJsonNode(requestBody).get("medias"), Media.class);
            return mediaService.updateMultiMediaStatus(mediaList);
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
