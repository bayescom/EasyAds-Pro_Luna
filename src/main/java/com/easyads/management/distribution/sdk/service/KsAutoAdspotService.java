package com.easyads.management.distribution.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.easyads.component.CONST;
import com.easyads.component.exception.AutoAdspotException;
import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.component.utils.CsjJsonUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.component.utils.TimeUtils;
import com.easyads.management.distribution.auto_adspot.ks_platform.model.*;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KsAutoAdspotService {
    @Autowired
    private SdkChannelMapper sdkChannelMapper;

    public KsKeyPair parseSecurityKeyAndAccessKey(SdkChannel sdkChannel) throws Exception {
        try {
            String access_key = sdkChannel.getReportApiParam().getChannelParams().get("access_key");
            String security_key = sdkChannel.getReportApiParam().getChannelParams().get("security_key");
            return new KsKeyPair(access_key, security_key);
        } catch (Exception e) {
            throw new AutoAdspotException("账户解析错误，请检查密钥和账户ID是否填写正确");
        }
    }


    private String getRequestBodyString(String requestBody, SdkChannel sdkChannel, Integer adspotType) {
        // adspotType /** 1 - 开屏， 2 信息流， 3 横幅， 4 插屏， 5 激励视频 */
        String appId = sdkChannel.getParams().getMediaId();

        String bodyString = null;

        switch (adspotType) {
            case CONST.EASYADS_BANNER:
                KsBannerCreate ksBannerCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsBannerCreate.class);
                ksBannerCreate.setAppId(appId);
                bodyString = CsjJsonUtils.toJson(ksBannerCreate);
                break;
            case CONST.EASYADS_SPLASH:
                KsSplashCreate ksSplashCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsSplashCreate.class);
                ksSplashCreate.setAppId(appId);
                bodyString = CsjJsonUtils.toJson(ksSplashCreate);
                break;
            case CONST.EASYADS_INTERSTITIAL:
                KsInterstitialCreate ksInterstitialCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsInterstitialCreate.class);
                ksInterstitialCreate.setAppId(appId);
                bodyString = CsjJsonUtils.toJson(ksInterstitialCreate);
                break;
            case CONST.EASYADS_FEED:
                KsFeedCreate ksFeedCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsFeedCreate.class);
                ksFeedCreate.setAppId(appId);
                bodyString = CsjJsonUtils.toJson(ksFeedCreate);
                break;
            case CONST.EASYADS_INCENTIVE:
                KsIncentiveCreate ksIncentiveCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsIncentiveCreate.class);
                ksIncentiveCreate.setAppId(appId);
                bodyString = CsjJsonUtils.toJson(ksIncentiveCreate);
                break;
        }
        return bodyString;
    }

    public JsonNode getOneKsAdspotSdkChannel(SdkChannel sdkChannel, Integer sdkChannelId, Long adspotId, Integer adspotType) throws Exception {
        // 会遇到先创建出来的广告源，然后再在广告网络中将他对应的账户给删掉，或者在广告网络中将对应的账户的【自动创建广告源】按钮给关闭（dkChannel.getReportApiParam().getAutoCreateStatus() == 0），的情况，在获取签名的时候会报错
        // 产品需求：有账户，可查询 更改 发送请求到百度 无账户 使用数据库存储数据
        if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
            JsonNode autoAdspot = JsonUtils.getJsonNode(sdkChannel.getSupplierAdspotConfig());
            return autoAdspot;
        } else {
            Long positionId = Long.parseLong(sdkChannel.getParams().getAdspotId());

            KsKeyPair keys = parseSecurityKeyAndAccessKey(sdkChannel);
            String accessKey = keys.getAccessKey();
            String securityKey = keys.getSecurityKey();

            // 这里是通过接口拿到最新的数据，
            KsApiClient client = new KsApiClient(accessKey, securityKey);

            KsQuery ksQuery  = new KsQuery();
            ksQuery.setPositionId(positionId);
            String bodyString = CsjJsonUtils.toJson(ksQuery);
            String responseJson = client.post(CONST.KS_QUERY_URI, bodyString);

            JsonNode response = JsonUtils.getJsonNode(responseJson);

            int code = response.path("result").asInt();
            if (code != 1) {
                String message = response.path("error_msg").asText();
                throw new AutoAdspotException(message);
            }

            JsonNode firstAdspot = response.path("data").get(0);
            if (adspotType == 1) {
                // 只有开屏的时候 countdownShow （跳过按钮是否显示倒计时）这个字段在get 的时候获取不到，因此要取在新建的时候我们数据库中存的
                // 这个是数据库存的
                KsSplashCreate originKsSplashCreate = JSONObject.parseObject(sdkChannel.getSupplierAdspotConfig(), KsSplashCreate.class);
                // 这个是 api get 接口获取的
                ObjectNode modifiedAdspot = (ObjectNode) firstAdspot;
                modifiedAdspot.put("countdownShow", originKsSplashCreate.getCountdownShow());
                sdkChannelMapper.updateOneAdspotSdkChannelSupplierAdspotConfig(adspotId, sdkChannelId, sdkChannel);
                return modifiedAdspot;
            } else {
                // 每次get的时候，都拿到最新的广告源，塞到数据库中，用来进行更新的时候对cpm的对比
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(firstAdspot));
                sdkChannelMapper.updateOneAdspotSdkChannelSupplierAdspotConfig(adspotId, sdkChannelId, sdkChannel);
                return response.path("data").get(0);
            }

        }
    }

    public void createOneKsAdspotSdkChannel(SdkChannel sdkChannel, Integer adspotType, String requestBody) throws Exception {
        // 实时竞价和固价，需要根据前端是否传了cpmFloor 来确定，传了就是固价，不传就是竞价，且不允许编辑。
        KsKeyPair keys = parseSecurityKeyAndAccessKey(sdkChannel);
        String accessKey = keys.getAccessKey();
        String securityKey = keys.getSecurityKey();

        // 这里是通过接口拿到最新的数据，
        KsApiClient client = new KsApiClient(accessKey, securityKey);

        String bodyString = getRequestBodyString(requestBody, sdkChannel, adspotType);
        String responseJson = client.post(CONST.KS_CREATE_URI, bodyString);
        JsonNode response = JsonUtils.getJsonNode(responseJson);

        int code = response.path("result").asInt();
        if (code != 1) {
            String message = response.path("error_msg").asText();
            throw new AutoAdspotException(message);
        }

        // 将他存到数据库中
        switch(adspotType) {
            case CONST.EASYADS_BANNER:
                KsBannerCreate ksBannerCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsBannerCreate.class);
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksBannerCreate));

                break;
            case CONST.EASYADS_SPLASH:
                KsSplashCreate ksSplashCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsSplashCreate.class);
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksSplashCreate));

                break;
            case CONST.EASYADS_INTERSTITIAL:
                KsInterstitialCreate ksInterstitialCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsInterstitialCreate.class);

                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksInterstitialCreate));
                break;
            case CONST.EASYADS_FEED:
                KsFeedCreate ksFeedCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsFeedCreate.class);
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksFeedCreate));

                break;
            case CONST.EASYADS_INCENTIVE:
                KsIncentiveCreate ksIncentiveCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsIncentiveCreate.class);

                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksIncentiveCreate));
                break;
        }
        sdkChannel.getParams().setAdspotId(response.path("data").asText());
    }

    public void updateOneKsAdspotSdkChannel(SdkChannel sdkChannel, SdkChannel originSdkChannel,  String requestBody, int adspotType) throws Exception {
        // 因为快手更新cpm 的接口，cpm 是必填的。但是前端更新接口的时候，可能就是保持 不设置cpm ，此时cpm 没有值，在快手哪里就会报错，因此判断一下，如果cpm 为空，就直接不走快手更新cpm那个接口
        KsCpmUpdate ksCpmUpdate = JSONObject.parseObject(requestBody).getObject("auto_adspot", KsCpmUpdate.class);

        // 如果当前账户为空，那么取数据库中的
        if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
            sdkChannel.setSupplierAdspotConfig(originSdkChannel.getSupplierAdspotConfig());
        } else {
            if (ksCpmUpdate.getCpmFloor() != null) {
                KsKeyPair keys = parseSecurityKeyAndAccessKey(sdkChannel);
                String accessKey = keys.getAccessKey();
                String securityKey = keys.getSecurityKey();

                Long adspotId = Long.parseLong(sdkChannel.getParams().getAdspotId());
                String appId = sdkChannel.getParams().getMediaId();

                ksCpmUpdate.setPositionId(adspotId);
                ksCpmUpdate.setAppId(appId);

                // 这里是通过接口拿到最新的数据，
                KsApiClient client = new KsApiClient(accessKey, securityKey);

                String bodyString = CsjJsonUtils.toJson(ksCpmUpdate);
                String responseJson = client.post(CONST.KS_UPDATE_URI, bodyString);
                JsonNode response = JsonUtils.getJsonNode(responseJson);

                int code = response.path("result").asInt();

                if (code != 1) {
                    String message = response.path("error_msg").asText();
                    throw new AutoAdspotException(message);
                }

                // 对比一样cpm 的值，如果是相同的，就不用改时间了
                Double originCpmFloor = JsonUtils.getJsonNode(originSdkChannel.getSupplierAdspotConfig()).path("cpm_floor").asDouble();
                if (!originCpmFloor.equals(ksCpmUpdate.getCpmFloor())) {
                    sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                }

                switch(adspotType) {
                    case CONST.EASYADS_BANNER:
                        KsBannerCreate ksBannerCreate = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), KsBannerCreate.class);

                        ksBannerCreate.setExpectCpm(ksCpmUpdate.getCpmFloor());
                        ksBannerCreate.setCpmFloor(ksCpmUpdate.getCpmFloor());

                        sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksBannerCreate));
                        break;

                    case CONST.EASYADS_SPLASH:
                        KsSplashCreate ksSplashCreate = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), KsSplashCreate.class);
                        ksSplashCreate.setExpectCpm(ksCpmUpdate.getCpmFloor());
                        ksSplashCreate.setCpmFloor(ksCpmUpdate.getCpmFloor());
                        sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksSplashCreate));

                        break;
                    case CONST.EASYADS_INTERSTITIAL:
                        KsInterstitialCreate ksInterstitialCreate = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), KsInterstitialCreate.class);
                        ksInterstitialCreate.setExpectCpm(ksCpmUpdate.getCpmFloor());
                        ksInterstitialCreate.setCpmFloor(ksCpmUpdate.getCpmFloor());
                        sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksInterstitialCreate));

                        break;
                    case CONST.EASYADS_FEED:
                        KsFeedCreate ksFeedCreate = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), KsFeedCreate.class);
                        ksFeedCreate.setExpectCpm(ksCpmUpdate.getCpmFloor());
                        ksFeedCreate.setCpmFloor(ksCpmUpdate.getCpmFloor());
                        sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksFeedCreate));

                        break;
                    case CONST.EASYADS_INCENTIVE:
                        KsIncentiveCreate ksIncentiveCreate =JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), KsIncentiveCreate.class);
                        ksIncentiveCreate.setExpectCpm(ksCpmUpdate.getCpmFloor());
                        ksIncentiveCreate.setCpmFloor(ksCpmUpdate.getCpmFloor());
                        sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(ksIncentiveCreate));

                        break;
                }
            } else {
                // 当是 cpm 为空的时候，不走 cpm 更新的接口，直接手动把数据库中取的值，再塞回去就好了。这里一定要有这个set的过程，否则 updateOneAdspotSdkChannel 的时候，sdkChannel 里面的 supplierAdspotConfig 是空
                sdkChannel.setSupplierAdspotConfig(originSdkChannel.getSupplierAdspotConfig());
            }
        }
    }

}
