package com.easyads.management.user.controller;


import com.easyads.component.exception.BadRequestException;
import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.user.model.user.User;
import com.easyads.management.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/user")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Object getUserList(@RequestParam Map<String, Object> queryParams,
                              HttpServletRequest request, HttpServletResponse response) {
        try {
            return userService.getUserList(queryParams);
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

    @GetMapping("/{userId}")
    public Object getOneUser(@PathVariable Long userId, HttpServletRequest request, HttpServletResponse response) {
        try {
            return userService.getOneUser(userId);
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
    public Object createOneUser(@RequestBody String requestBody,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("user"), User.class);
            response.setStatus(201);
            return userService.createOneUser(user);
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

    @PutMapping("/{userId}")
    public Object updateOneUser(@PathVariable Long userId, @RequestBody String requestBody,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("user"), User.class);
            return userService.updateOneUser(userId, user);
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

    @PutMapping("/status/{id}")
    public Object updateOneUserStatus(@PathVariable Long id, @RequestBody String requestBody,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            int status = JsonUtils.getJsonNode(requestBody).get("status").asInt();
            return userService.updateOneUserStatus(id, status);
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

    @PutMapping("/password/{id}")
    public Object updateOneCustomerPassword(@PathVariable Long id, @RequestBody String requestBody,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            String password = JsonUtils.getJsonNode(requestBody).get("password").asText();
            return userService.updateOneUserPassword(id, password);
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

    @DeleteMapping("/{id}")
    public Object deleteOneUser(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(204);
            return userService.deleteOneUser(id);
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

    @GetMapping("/role_type")
    public Object getUserRoleType(@RequestParam int userRoleType,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            return userService.getUserType(userRoleType);
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
