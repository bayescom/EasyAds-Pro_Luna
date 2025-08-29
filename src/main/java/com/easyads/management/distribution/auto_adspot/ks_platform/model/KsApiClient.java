package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import com.easyads.component.CONST;
import okhttp3.*;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class KsApiClient {
    private final String securityKey;
    private final String accessKey;
    private final OkHttpClient httpClient;

    /**
     * 构造函数(使用默认host)
     * @param accessKey 访问密钥
     * @param securityKey 安全密钥
     */
    public KsApiClient(String accessKey, String securityKey) {
        this(CONST.KS_DEFAULT_HOST, accessKey, securityKey);
    }

    /**
     * 构造函数
     * @param host API主机地址
     * @param accessKey 访问密钥
     * @param securityKey 安全密钥
     */
    public KsApiClient(String host, String accessKey, String securityKey) {
        this.accessKey = Objects.requireNonNull(accessKey, "AccessKey不能为空");
        this.securityKey = Objects.requireNonNull(securityKey, "SecurityKey不能为空");

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 执行POST请求
     * @param apiPath API路径
     * @param content JSON请求体
     * @return 响应字符串
     * @throws IOException
     */
    public String post(String apiPath, String content) throws IOException {
        String url = generateSignedUrl(apiPath);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, content);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
                .build();

        return executeRequest(request);
    }

    /**
     * 执行请求并返回响应
     * @param request OkHttp请求对象
     * @return 响应字符串
     * @throws IOException
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("广告平台配置信息错误，请确认当前配置信息是否填写有误；若无权限请与三方广告平台沟通并开通权限。");
            }
            ResponseBody body = response.body();
            return body != null ? body.string() : "";
        }
    }

    /**
     * 生成带签名的URL
     * @param apiPath API路径
     * @return 带签名的完整URL
     */
    private String generateSignedUrl(String apiPath) {
        // 确保每次请求使用新的时间戳
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        // 添加ak到参数中
        requestParams.put("ak", accessKey);

        // 复制参数用于签名计算(包含sk)
        Map<String, String> signParams = new HashMap<>(requestParams);
        signParams.put("sk", securityKey);

        // 生成签名
        String sign = generateSign(apiPath, signParams);

        // 构建最终URL(不包含sk)
        StringBuilder urlBuilder = new StringBuilder(CONST.KS_DEFAULT_HOST)
                .append(apiPath)
                .append("?");

        // 添加参数(不需要排序)
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            urlBuilder.append(entry.getKey())
                    .append("=")
                    .append(urlEncode(entry.getValue()))
                    .append("&");
        }

        // 添加签名
        urlBuilder.append("sign=").append(sign);

        return urlBuilder.toString();
    }

    /**
     * 生成签名(使用MD5Digest)
     * @param apiPath API路径
     * @param params 包含sk的所有参数
     * @return MD5签名
     */
    private String generateSign(String apiPath, Map<String, String> params) {
        // 按字典序排序参数名
        List<String> paramNames = new ArrayList<>(params.keySet());
        Collections.sort(paramNames);

        // 构建待签名字符串
        StringBuilder signString = new StringBuilder(apiPath).append("?");

        for (String paramName : paramNames) {
            signString.append(paramName)
                    .append("=")
                    .append(params.get(paramName))
                    .append("&");
        }

        // 去掉最后的&
        if (signString.length() > 0) {
            signString.setLength(signString.length() - 1);
        }

        // 使用MD5Digest计算MD5
        return md5WithDigest(signString.toString());
    }

    /**
     * 使用MD5Digest计算MD5值
     * @param input 输入字符串
     * @return 32位小写MD5
     */
    private String md5WithDigest(String input) {
        MD5Digest digest = new MD5Digest();
        byte[] inputBytes = input.getBytes();
        digest.update(inputBytes, 0, inputBytes.length);

        byte[] md5Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(md5Bytes, 0);

        return Hex.toHexString(md5Bytes);
    }

    /**
     * URL编码
     * @param value 待编码值
     * @return 编码后的字符串
     */
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
}
