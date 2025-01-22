package com.easyads.component.utils;

import com.easyads.management.user.model.user.UserPassword;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecurityUtils {
    public static UserPassword getUserAuthentication(String authToken) {
        // 去掉 "Basic " 前缀，并对token进行Base64解码
        String tokenInfo = authToken.substring("Basic ".length());
        byte[] decodedBytes = Base64.getDecoder().decode(tokenInfo);

        // 分割用户名和密码
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] credentials = decodedString.split(":");

        if (credentials.length != 2) {
            throw new IllegalArgumentException("Invalid Basic Auth Token format");
        }

        return new UserPassword(credentials[0], credentials[1]);
    }
}
