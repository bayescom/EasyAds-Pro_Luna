package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

public class BdUbapiClient {
    private String host;
    private PrivateKey privateKey;
    private String accessKey;

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 读取privateKey
     * @param pemKey
     * @return
     */
    public static PrivateKey parsePEMPrivateKey(String pemKey) {
        try (PEMParser pemParser = new PEMParser(new StringReader(pemKey))) {
            Object object = pemParser.readObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else if (object instanceof PEMKeyPair) {
                return converter.getKeyPair((PEMKeyPair) object).getPrivate();
            } else if (object instanceof KeyPair) {
                return ((KeyPair) object).getPrivate();
            } else {
                throw new RuntimeException("private key 不正确");
            }
        } catch (IOException e) {
            throw new RuntimeException("private key 不正确");
        }
    }

    public static void notNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("账户参数不正确");
        }
    }

    /**
     * 签名信息
     * @param privateKey
     * @param message
     * @return
     */
    public static byte[] signMessage(PrivateKey privateKey, byte[] message) {
        notNull(privateKey);
        notNull(message);

        Signature dsa;
        try {
            dsa = Signature.getInstance("SHA1withDSA", "SUN");
            dsa.initSign(privateKey);
            dsa.update(message);
            byte[] result = dsa.sign();
            return result;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public BdUbapiClient(String host, String accessKey, String dsaPrivateKey) {
        this.host = host;
        this.accessKey = accessKey;
        this.privateKey = parsePEMPrivateKey(dsaPrivateKey);
    }

    public HttpResponse get(String uri) throws IOException {
        String method = "GET";
        return execute(method, uri, null, null);
    }

    public HttpResponse post(String uri, String contentType, byte[] content) throws IOException {
        return execute("POST", uri, contentType, content);
    }

    public HttpResponse put(String uri, String contentType, byte[] content) throws IOException {
        return execute("PUT", uri, contentType, content);
    }

    private HttpResponse execute(String method, String uri, String contentType, byte[] content) throws IOException {
        Long timeStamp = System.currentTimeMillis();

        // 1. 准备签名需要的数据
        List<String> itemsToBeSinged = new ArrayList<String>(6);
        // 1.1 accessKey
        itemsToBeSinged.add(accessKey);
        // 1.2 method
        itemsToBeSinged.add(method);
        // 1.3 uri
        itemsToBeSinged.add(uri);
        // 1.4 时间
        itemsToBeSinged.add(Long.toString(timeStamp));
        // 1.5 contentType
        // 1.6 body 的md5
        if (contentType != null && content != null) {
            itemsToBeSinged.add(contentType);

            MD5Digest digest = new MD5Digest();
            byte[] contentBytes = content;
            digest.update(contentBytes, 0, contentBytes.length);
            byte[] digestBytes = new byte[digest.getDigestSize()];
            digest.doFinal(digestBytes, 0);
            itemsToBeSinged.add(new String(Hex.encode(digestBytes)));
        } else {
            itemsToBeSinged.add(""); // empty ContentType
            itemsToBeSinged.add(""); // empty content md5
        }

        // 2. 签名
        String stringTobeSigned = StringUtils.join(itemsToBeSinged, "\n");
        byte[] signatureBytes = signMessage(privateKey, stringTobeSigned.getBytes());

        String signature = new String(Base64.encode(signatureBytes));

        // 3. 发起请求
        RequestBuilder requestBuilder = RequestBuilder.create(method);
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setSocketTimeout(60000)
                .setConnectTimeout(30000)
                .setConnectionRequestTimeout(5000);
        RequestConfig config = configBuilder.build();

        requestBuilder.setConfig(config);
        requestBuilder.setUri(host + uri);
        requestBuilder.setVersion(HttpVersion.HTTP_1_1);


        requestBuilder.addHeader("x-ub-date", Long.toString(timeStamp));
        requestBuilder.addHeader("x-ub-authorization", accessKey + ":" + signature);

        if (contentType != null) {
            requestBuilder.addHeader("Content-Type", contentType);
        }
        if (content != null) {
            requestBuilder.setEntity(new ByteArrayEntity(content));
        }

        HttpResponse response = HttpClientBuilder.create().build().execute(requestBuilder.build());
        return response;
    }

}
