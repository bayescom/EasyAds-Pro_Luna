package com.easyads.management.user.controller;

import com.easyads.component.rpc.ResponseCodeUtils;
import com.easyads.component.exception.ForbiddenException;
import com.easyads.component.exception.NoResourceException;
import com.easyads.component.utils.SecurityUtils;
import com.easyads.management.user.model.user.UserPassword;
import com.easyads.management.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value="/user")
public class UserLoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public Object userLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            String tokenStr = request.getHeader("Authorization");
            UserPassword userPassword = SecurityUtils.getUserAuthentication(tokenStr);
            return userService.userLogin(userPassword);
        } catch (NoResourceException nre) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("message", nre.getMessage());
        } catch (ForbiddenException fe) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("message", fe.getMessage());
        } catch (Exception e) {
            response.setStatus(500);
            request.setAttribute("message", "服务器内部异常");
            log.error("error = {}", e);
        }

        return ResponseCodeUtils.setResponseErrorCodeWithMessage(response.getStatus(), (String) request.getAttribute("message"));
    }
}
