package com.easyads.management.distribution.auto_adspot.ylh_platform.model;

import org.apache.commons.codec.digest.DigestUtils;
import com.easyads.component.utils.TimeUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class YlhSignature {
    /**
     * 生成签名
     * @param memberId 账号账户ID
     * @param secret 约定的密钥
     * @param timestamp 时间戳(秒)
     * @return SHA1签名
     */
    public static String generateSignature(long memberId, String secret, int timestamp) {
        // 验证参数
        if (memberId <= 0) {
            throw new IllegalArgumentException("memberId不能为空");
        }
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("secret不能为空");
        }
        if (timestamp <= 0) {
            throw new IllegalArgumentException("timestamp必须大于0");
        }

        // 将参数转为字符串并排序
        List<String> strings = Arrays.asList(
                Long.toString(memberId),
                secret,
                Integer.toString(timestamp)
        );
        Collections.sort(strings);

        // 拼接字符串
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
        }

        // 生成SHA1签名
        return DigestUtils.sha1Hex(memberId + secret + timestamp);
    }

    // 计算token
    public static String calcToken(long memberId, String secret) {
        // 1. 获取sign
        int timestamp = TimeUtils.getCurrentTimestamp().intValue();
        String sign = generateSignature(memberId, secret, timestamp);
        // 2. 获取token  规范：base64('memberId', 'time', 'sign')
        String plain = String.format("%d,%s,%s", memberId, timestamp, sign);

        return Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    }
}
