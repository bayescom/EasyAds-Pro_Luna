package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CSJ平台签名生成工具
 */
public class CsjSignature {
    /**
     * 生成签名
     * @param securityKey 账号security-key
     * @param timestamp 时间戳(秒)
     * @param nonce 随机盐值
     * @return SHA1签名
     */
    public static String generateSignature(String securityKey, int timestamp, int nonce) {
        // 验证参数
        if (securityKey == null || securityKey.isEmpty()) {
            throw new IllegalArgumentException("securityKey不能为空");
        }
        if (timestamp <= 0) {
            throw new IllegalArgumentException("timestamp必须大于0");
        }
        if (nonce <= 0) {
            throw new IllegalArgumentException("nonce必须大于0");
        }

        // 将参数转为字符串并排序
        List<String> strings = Arrays.asList(
                securityKey,
                Integer.toString(timestamp),
                Integer.toString(nonce)
        );
        Collections.sort(strings);

        // 拼接字符串
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
        }

        // 生成SHA1签名
        return DigestUtils.sha1Hex(sb.toString());
    }

}
