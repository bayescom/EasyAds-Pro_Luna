package com.easyads.management.distribution.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.easyads.component.CONST;
import com.easyads.component.exception.AutoAdspotException;
import com.easyads.component.utils.CsjJsonUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.component.utils.TimeUtils;
import com.easyads.management.distribution.auto_adspot.csj_platform.model.*;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.*;
import okhttp3.*;


@Service
public class CsjAutoAdspotService {

    // 放到类的变量定义中
    private final OkHttpClient client = new OkHttpClient();

    private String send_request(String url, String content) throws AutoAdspotException {
        try {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, content);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            return responseBody;
        } catch (IOException e) {
            throw new AutoAdspotException("请求穿山甲平台失败");
        }
    }

    // 设置 请求的user_id role_id timestamp sign 密钥参数
    private void setCsjSignFromDb(CsjBase csj) throws AutoAdspotException {
        try {
            // 设置签名
            int timestamp = TimeUtils.getCurrentTimestamp().intValue();
            csj.setTimestamp(timestamp);
            String sign = CsjSignature.generateSignature(csj.getSecurityKey(),timestamp,csj.getNonce());
            csj.setSign(sign);
        } catch (Exception e) {
            throw new AutoAdspotException("解析穿山甲参数失败: " + e.getMessage());
        }
    }

    private String getRequestBodyString(String requestBody, SdkChannel sdkChannel, Integer adspotType) throws AutoAdspotException {
        // adspotType /** 1 - 开屏， 2 信息流， 3 横幅， 4 插屏， 5 激励视频 */
        Integer appId = Integer.valueOf(sdkChannel.getParams().getMediaId());
        String securityKey = sdkChannel.getReportApiParam().getChannelParams().get("security_key");
        Integer userId = Integer.valueOf(sdkChannel.getReportApiParam().getChannelParams().get("user_id"));
        Integer roleId = Integer.valueOf(sdkChannel.getReportApiParam().getChannelParams().get("role_id"));

        String bodyString = null;

        try {
            switch(adspotType) {
                case CONST.EASYADS_BANNER :
                    CsjBannerCreate csjBannerCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjBannerCreate.class);

                    // 设置 穿山甲请求 token
                    csjBannerCreate.setUserId(userId);
                    csjBannerCreate.setRoleId(roleId);
                    csjBannerCreate.setSecurityKey(securityKey);

                    setCsjSignFromDb(csjBannerCreate);

                    // 我们的广告位类型，转成穿山甲对应的广告位类型
                    csjBannerCreate.setAdSlotType(adspotType);
                    csjBannerCreate.setAppId(appId);

                    csjBannerCreate.calWidth();
                    csjBannerCreate.calHeight();
                    bodyString = CsjJsonUtils.toJson(csjBannerCreate);
                    break;

                case CONST.EASYADS_SPLASH :
                    CsjSplashCreate csjSplashCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjSplashCreate.class);

                    // 设置 穿山甲请求 token
                    csjSplashCreate.setUserId(userId);
                    csjSplashCreate.setRoleId(roleId);
                    csjSplashCreate.setSecurityKey(securityKey);
                    setCsjSignFromDb(csjSplashCreate);

                    csjSplashCreate.setAdSlotType(adspotType);
                    csjSplashCreate.setAppId(appId);
                    bodyString = CsjJsonUtils.toJson(csjSplashCreate);
                    break;

                case CONST.EASYADS_INTERSTITIAL :
                    CsjInterstitialCreate csjInterstitialCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjInterstitialCreate.class);

                    // 设置 穿山甲请求 token
                    csjInterstitialCreate.setUserId(userId);
                    csjInterstitialCreate.setRoleId(roleId);
                    csjInterstitialCreate.setSecurityKey(securityKey);
                    setCsjSignFromDb(csjInterstitialCreate);

                    csjInterstitialCreate.setAdSlotType(adspotType);
                    csjInterstitialCreate.setAppId(appId);
                    bodyString = CsjJsonUtils.toJson(csjInterstitialCreate);
                    break;

                case CONST.EASYADS_FEED:
                    CsjFeedCreate csjFeedCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjFeedCreate.class);

                    // 设置 穿山甲请求 token
                    csjFeedCreate.setUserId(userId);
                    csjFeedCreate.setRoleId(roleId);
                    csjFeedCreate.setSecurityKey(securityKey);
                    setCsjSignFromDb(csjFeedCreate);

                    csjFeedCreate.setAdSlotType(adspotType);
                    csjFeedCreate.setAppId(appId);
                    bodyString = CsjJsonUtils.toJson(csjFeedCreate);

                    break;

                case CONST.EASYADS_INCENTIVE :
                    CsjIncentiveCreate csjIncentiveCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjIncentiveCreate.class);

                    // 设置 穿山甲请求 token
                    csjIncentiveCreate.setUserId(userId);
                    csjIncentiveCreate.setRoleId(roleId);
                    csjIncentiveCreate.setSecurityKey(securityKey);
                    setCsjSignFromDb(csjIncentiveCreate);

                    csjIncentiveCreate.setAdSlotType(adspotType);
                    csjIncentiveCreate.setAppId(appId);
                    bodyString = CsjJsonUtils.toJson(csjIncentiveCreate);
                    break;
            }
        } catch (Exception e) {
            // 处理异常情况，例如返回默认值或重新抛出异常
            throw new AutoAdspotException("广告位类型不正确");
        }

        return bodyString;
    }

    // cpm 更新之后穿山甲返回的 code 状态，成功不是返回的0， 而是PG0000
    public static int parseResponseCpmUpdateCode(Integer code) {
        try {
            if (code instanceof Integer) {
                return (Integer) code;
            };
            if ("PG0000".equals(code)) {
                return 0;
            };
            return Integer.parseInt(code.toString());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getOneCsjAdSlotName(SdkChannel sdkChannel) throws AutoAdspotException {
        CsjQuery csjQuery = new CsjQuery();
        Integer adSlotId = Integer.valueOf(sdkChannel.getParams().getAdspotId());

        String securityKey = sdkChannel.getReportApiParam().getChannelParams().get("security_key");
        Integer userId = Integer.valueOf(sdkChannel.getReportApiParam().getChannelParams().get("user_id"));
        Integer roleId = Integer.valueOf(sdkChannel.getReportApiParam().getChannelParams().get("role_id"));

        csjQuery.setUserId(userId);
        csjQuery.setRoleId(roleId);
        csjQuery.setSecurityKey(securityKey);
        csjQuery.setAdSlotId(adSlotId);

        setCsjSignFromDb(csjQuery);

        try {
            // 发送请求并获取响应
            String url = CONST.CSJ_URL + "code/query";
            String responseJson = send_request(url, CsjJsonUtils.toJson(csjQuery));

            JsonNode response = JsonUtils.getJsonNode(responseJson);
            // 1. 先获取data对象

            int code = response.path("code").asInt();
            if (code != 0) {
                throw new AutoAdspotException("获取穿山甲广告位失败");
            }
            JsonNode data = response.path("data");
            // 3. 获取ad_slot_list数组
            JsonNode adSlotListNode = data.path("ad_slot_list");

            // 4. 检查数组是否为空或不存在
            if (adSlotListNode == null || adSlotListNode.isNull() || !adSlotListNode.isArray() || adSlotListNode.size() == 0) {
                throw new AutoAdspotException("获取穿山甲广告位失败");
            }

            // 5. 获取第一个广告位
            JsonNode firstAdSlot = adSlotListNode.get(0);

            return firstAdSlot.path("ad_slot_name").asText();
        } catch (Exception e) {
            throw new AutoAdspotException("解析穿山甲返回数据失败: " + e.getMessage());
        }
    }

    // 只有getOne 和 更新，编辑才需要单独出来
    public JsonNode getOneCsjAdspotSdkChannel(SdkChannel sdkChannel) throws Exception {
        JsonNode csjAdspot = JsonUtils.getJsonNode(sdkChannel.getSupplierAdspotConfig());
        return csjAdspot;
    }

    // 这里，45001 表示 应用ID 不正确，但是他不一定是外层code返回的，可能存在 【某些复杂场景下，返回信息中会包含内部码（internal code）以对返回码做更详细的解释说明 -- 摘自穿山甲】 。
    public static int createErrorCode(int originalCode, String message) {
        if (message == null) {
            return originalCode;
        }
        // 使用正则检查是否包含目标code
        boolean containsTargetCode = Pattern.compile("Internal code:\\[.*\\b" + 45001 + "\\b.*\\]")
                .matcher(message)
                .find();
        return containsTargetCode ? 45001 : originalCode;
    }


    public void createOneCsjAdspotSdkChannel(SdkChannel sdkChannel, Integer adspotType,String requestBody) throws Exception {
        String bodyString = getRequestBodyString(requestBody, sdkChannel, adspotType);

        String url = CONST.CSJ_URL + "code/create";
        String responseJson = send_request(url, bodyString);

        JsonNode response = JsonUtils.getJsonNode(responseJson);

        // 1. 先获取data对象
        // 获取最外层code和message
        int code = response.path("code").asInt();
        String message = response.path("message").asText();

        // 用于存储最终使用的code
        int finalCode = createErrorCode(code, message);

        if (finalCode != 0) {
            if (finalCode == 45001) {
                throw new AutoAdspotException("配置的应用ID不在关联的穿山甲账号下，或当前应用在穿山甲后台的合作状态异常，请登录穿山甲账号确认配置是否正确。");
            } else {
                throw new AutoAdspotException("广告平台配置信息错误，请确认当前配置信息是否填写有误；若无权限请与三方广告平台沟通并开通权限。");
            }
        }

        JsonNode data = response.path("data");
        sdkChannel.getParams().setAdspotId(data.path("ad_slot_id").asText());

        switch(adspotType) {
            case CONST.EASYADS_BANNER:
                CsjBannerCreate csjBannerCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjBannerCreate.class);

                // 创建成功之后，穿山甲的接口是不返回代码位的名称的，因此如果前端没传代码位名称，需要根据代码位ID 去穿山甲查
                if (csjBannerCreate.getAdSlotName() == null) {
                    String adSlotName = getOneCsjAdSlotName(sdkChannel);
                    csjBannerCreate.setAdSlotName(adSlotName);
                }

                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjBannerCreate));

                break;
            case CONST.EASYADS_SPLASH:
                CsjSplashCreate csjSplashCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjSplashCreate.class);

                if (csjSplashCreate.getAdSlotName() == null) {
                    String adSlotName = getOneCsjAdSlotName(sdkChannel);
                    csjSplashCreate.setAdSlotName(adSlotName);
                }
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjSplashCreate));

                break;
            case CONST.EASYADS_INTERSTITIAL:
                CsjInterstitialCreate csjInterstitialCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjInterstitialCreate.class);

                if (csjInterstitialCreate.getAdSlotName() == null) {
                    String adSlotName = getOneCsjAdSlotName(sdkChannel);
                    csjInterstitialCreate.setAdSlotName(adSlotName);
                }
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjInterstitialCreate));

                break;
            case CONST.EASYADS_FEED:
                CsjFeedCreate csjFeedCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjFeedCreate.class);

                if (csjFeedCreate.getAdSlotName() == null) {
                    String adSlotName = getOneCsjAdSlotName(sdkChannel);
                    csjFeedCreate.setAdSlotName(adSlotName);
                }
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjFeedCreate));

                break;
            case CONST.EASYADS_INCENTIVE:
                CsjIncentiveCreate csjIncentiveCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjIncentiveCreate.class);

                if (csjIncentiveCreate.getAdSlotName() == null) {
                    String adSlotName = getOneCsjAdSlotName(sdkChannel);
                    csjIncentiveCreate.setAdSlotName(adSlotName);
                }
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjIncentiveCreate));
                break;
        }
    }


    public void updateOneCsjAdspotSdkChannel(SdkChannel sdkChannel, SdkChannel originSdkChannel,  String requestBody, int adspotType) throws Exception {
        // 因为穿山甲更新cpm 的接口，cpm 是必填的。但是前端更新接口的时候，可能就是保持 不设置cpm ，此时cpm 没有值，在穿山甲哪里就会报错，因此判断一下，如果cpm 为空，就直接不走穿山甲更新cpm那个接口
        CsjCpmUpdate csjCpmUpdate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(requestBody).get("auto_adspot"), CsjCpmUpdate.class);
        if (csjCpmUpdate.getCpm() != null) {
            Integer adSlotId = Integer.valueOf(sdkChannel.getParams().getAdspotId());
            Integer appId = Integer.valueOf(sdkChannel.getParams().getMediaId());

            // 设置 穿山甲请求 token
            if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
                throw new AutoAdspotException("账户名称下未配置相关参数");
            }
            String securityKey = sdkChannel.getReportApiParam().getChannelParams().get("security_key");

            Integer userId = Integer.valueOf(sdkChannel.getReportApiParam().getChannelParams().get("user_id"));
            Integer roleId = Integer.valueOf(sdkChannel.getReportApiParam().getChannelParams().get("role_id"));

            csjCpmUpdate.setUserId(userId);
            csjCpmUpdate.setRoleId(roleId);
            csjCpmUpdate.setSecurityKey(securityKey);

            setCsjSignFromDb(csjCpmUpdate);

            // 我们的广告位类型，转成穿山甲对应的广告位类型
            csjCpmUpdate.setAdSlotId(adSlotId);
            csjCpmUpdate.setAppId(appId);

            setCsjSignFromDb(csjCpmUpdate);
            String bodyString = CsjJsonUtils.toJson(csjCpmUpdate);

            //  发送请求并获取响应
            String url = CONST.PANGLE_CPM_URL + "code/cpm";

            String responseJson = send_request(url, bodyString);

            JsonNode response = JsonUtils.getJsonNode(responseJson);
            int responseCode = parseResponseCpmUpdateCode(response.path("code").asInt());

            if (responseCode != 0) {
                throw new AutoAdspotException("广告平台配置信息错误，请确认当前配置信息是否填写有误；若无权限请与三方广告平台沟通并开通权限。");
            }

            switch(adspotType) {
                case CONST.EASYADS_BANNER:
                    CsjBannerCreate csjBannerCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(originSdkChannel.getSupplierAdspotConfig()), CsjBannerCreate.class);
                    // 从10 =》 10，不应该认为是更新，穿山甲平台也不认为，只有他们不相等的时候，才更新cpm 的更新时间。
                    if (!csjCpmUpdate.getCpm().equals(csjBannerCreate.getCpm())) {
                        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                    }
                    csjBannerCreate.setCpm(csjCpmUpdate.getCpm());
                    sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjBannerCreate));

                    break;
                case CONST.EASYADS_SPLASH:
                    CsjSplashCreate csjSplashCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(originSdkChannel.getSupplierAdspotConfig()), CsjSplashCreate.class);
                    if (!csjCpmUpdate.getCpm().equals(csjSplashCreate.getCpm())) {
                        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                    }
                    csjSplashCreate.setCpm(csjCpmUpdate.getCpm());
                    sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjSplashCreate));

                    break;
                case CONST.EASYADS_INTERSTITIAL:
                    CsjInterstitialCreate csjInterstitialCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(originSdkChannel.getSupplierAdspotConfig()), CsjInterstitialCreate.class);
                    if (!csjCpmUpdate.getCpm().equals(csjInterstitialCreate.getCpm())) {
                        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                    }
                    csjInterstitialCreate.setCpm(csjCpmUpdate.getCpm());
                    sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjInterstitialCreate));

                    break;
                case CONST.EASYADS_FEED:
                    CsjFeedCreate csjFeedCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(originSdkChannel.getSupplierAdspotConfig()), CsjFeedCreate.class);
                    if (!csjCpmUpdate.getCpm().equals(csjFeedCreate.getCpm())) {
                        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                    }
                    csjFeedCreate.setCpm(csjCpmUpdate.getCpm());
                    sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjFeedCreate));

                    break;
                case CONST.EASYADS_INCENTIVE:
                    CsjIncentiveCreate csjIncentiveCreate = JsonUtils.convertJsonNodeToObject(JsonUtils.getJsonNode(originSdkChannel.getSupplierAdspotConfig()), CsjIncentiveCreate.class);
                    if (!csjCpmUpdate.getCpm().equals(csjIncentiveCreate.getCpm())) {
                        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                    }
                    csjIncentiveCreate.setCpm(csjCpmUpdate.getCpm());
                    sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(csjIncentiveCreate));

                    break;
            }
        } else {
            // 当是cpm 为空的时候，不走cpm 更新的接口，直接手动把数据库中取的值，再塞回去就好了。这里一定要有这个set的过程，否则 updateOneAdspotSdkChannel 的时候，sdkChannel 里面的 supplierAdspotConfig 是空
            sdkChannel.setSupplierAdspotConfig(originSdkChannel.getSupplierAdspotConfig());
        }
    }

}


