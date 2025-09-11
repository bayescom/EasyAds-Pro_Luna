package com.easyads.management.distribution.sdk.service;

import com.alibaba.fastjson.JSONObject;
import com.easyads.component.CONST;
import com.easyads.component.exception.AutoAdspotException;
import com.easyads.component.utils.CsjJsonUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.component.utils.TimeUtils;
import com.easyads.management.distribution.auto_adspot.ylh_platform.model.*;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class YlhAutoAdspotService {
    private final OkHttpClient client = new OkHttpClient();

    // 封装的公共请求函数
    private String send_request(String url, String content, String token) throws AutoAdspotException {
        try {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, content);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("token", token)
                    .addHeader("content-type", "application/json")
                    .addHeader("accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            return responseBody;
        } catch (IOException e) {
            throw new AutoAdspotException("请求优量汇平台失败");
        }
    }

    private String getRequestBodyString(String requestBody, SdkChannel sdkChannel, Integer adspotType) throws Exception {
        // adspotType /** 1 - 开屏， 2 信息流， 3 横幅， 4 插屏， 5 激励视频 */
        long appId = Long.parseLong(sdkChannel.getParams().getMediaId());
        long memberId = parseMemberId(sdkChannel);

        String bodyString = null;
        switch (adspotType) {
            case CONST.EASYADS_BANNER:
                YlhBannerCreate ylhBannerCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhBannerCreate.class);
                ylhBannerCreate.setBaseParams(appId, memberId, adspotType);
                // 模版渲染-图文组合时，优量汇只返回ad_crt_template_type，ad_crt_type_list需后端自己塞
                if (ylhBannerCreate.getRenderType().equals("TEMPLATE")) {
                    ylhBannerCreate.setAdCrtTypeList(new String[]{"IMAGE"});
                }
                bodyString = CsjJsonUtils.toJson(ylhBannerCreate);
                break;
            case CONST.EASYADS_SPLASH:
                YlhSplashCreate ylhSplashCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhSplashCreate.class);
                ylhSplashCreate.setBaseParams(appId, memberId, adspotType);
                bodyString = CsjJsonUtils.toJson(ylhSplashCreate);
                break;
            case CONST.EASYADS_INTERSTITIAL:
                YlhInterstitialCreate ylhInterstitialCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhInterstitialCreate.class);
                ylhInterstitialCreate.setBaseParams(appId, memberId, adspotType);
                bodyString = CsjJsonUtils.toJson(ylhInterstitialCreate);
                break;
            case CONST.EASYADS_FEED:
                YlhFeedCreate ylhFeedCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhFeedCreate.class);
                ylhFeedCreate.setBaseParams(appId, memberId, adspotType);
                bodyString = CsjJsonUtils.toJson(ylhFeedCreate);
                break;
            case CONST.EASYADS_INCENTIVE:
                YlhIncentiveCreate ylhIncentiveCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhIncentiveCreate.class);
                ylhIncentiveCreate.setBaseParams(appId, memberId, adspotType);
                bodyString = CsjJsonUtils.toJson(ylhIncentiveCreate);
                break;
        }
        return bodyString;
    }

    public long parseMemberId(SdkChannel sdkChannel) throws AutoAdspotException {
        try {
            return Long.parseLong(sdkChannel.getReportApiParam().getChannelParams().get("member_id"));
        } catch (Exception e) {
            throw new AutoAdspotException("账户解析错误，请检查密钥和账户ID是否填写正确");
        }
    }

    public JsonNode getOneYlhAdspotSdkChannel(SdkChannel sdkChannel) throws Exception {
        // 如果账户被删除，查询 倍联数据库存储数据
        if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
            return JsonUtils.getJsonNode(sdkChannel.getSupplierAdspotConfig());
        } else { // 有账户，发送查询请求到优量汇
            long memberId = parseMemberId(sdkChannel);
            String secret = sdkChannel.getReportApiParam().getChannelParams().get("secret");
            String placementId = sdkChannel.getParams().getAdspotId();

            YlhQuery ylhQuery = new YlhQuery(memberId, placementId);
            String bodyString = CsjJsonUtils.toJson(ylhQuery);

            String token = YlhSignature.calcToken(memberId, secret);
            String responseJson = send_request(CONST.YLH_URL_LIST, bodyString, token);

            JsonNode response = JsonUtils.getJsonNode(responseJson);
            int code = response.path("code").asInt();
            if (code != 0) {
                throw new AutoAdspotException(responseJson);
            }

            return response.path("data").path("list").get(0);
        }
    }

    public void createOneYlhAdspotSdkChannel(SdkChannel sdkChannel, Integer adspotType, String requestBody) throws Exception {
        long memberId = parseMemberId(sdkChannel);
        String secret = sdkChannel.getReportApiParam().getChannelParams().get("secret");

        String token = YlhSignature.calcToken(memberId, secret);

        String bodyString = getRequestBodyString(requestBody, sdkChannel, adspotType);
        String responseJson = send_request(CONST.YLH_URL_ADD, bodyString, token);

        JsonNode response = JsonUtils.getJsonNode(responseJson);
        int code = response.path("code").asInt();

        if (code != 0) {
            if (code == 130127) {
                throw new AutoAdspotException("配置的媒体ID不在关联的优量汇账号下，或当前媒体在优量汇后台的合作状态异常，请登录优量汇账号确认配置是否正确。");
            }

            if (code == 130109) {
                throw new AutoAdspotException("参数异常，请检查表单格式填写是否正确或联系技术支持咨询");
            }

            if (code == 11003) {
                throw new AutoAdspotException("广告平台配置信息错误，请确认当前配置信息是否填写有误；若无权限请与三方广告平台沟通并开通权限。");
            } else {
                throw new AutoAdspotException(responseJson);
            }
        }

        JsonNode data = response.path("data");
        sdkChannel.getParams().setAdspotId(data.path("placement_id").asText());
        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
        sdkChannel.setSupplierAdspotConfig(bodyString);
    }

    public void updateOneYlhAdspotSdkChannel(SdkChannel sdkChannel, SdkChannel originSdkChannel,  String requestBody, int adspotType) throws Exception {
        switch (adspotType) {
            // 优量汇字段更新有的必填，有的可不填，为了方便管理，创建的时候什么字段，更新的时候同样传回去
            case CONST.EASYADS_BANNER:
                YlhBannerCreate ylhBannerCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhBannerCreate.class);
                updateYlhAdspotSdkChannel(ylhBannerCreate, sdkChannel, originSdkChannel);
                break;
            case CONST.EASYADS_SPLASH:
                YlhSplashCreate ylhSplashCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhSplashCreate.class);
                updateYlhAdspotSdkChannel(ylhSplashCreate, sdkChannel, originSdkChannel);
                break;
            case CONST.EASYADS_INTERSTITIAL:
                YlhInterstitialCreate ylhInterstitialCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhInterstitialCreate.class);
                updateYlhAdspotSdkChannel(ylhInterstitialCreate, sdkChannel, originSdkChannel);
                break;
            case CONST.EASYADS_FEED:
                YlhFeedCreate ylhFeedCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhFeedCreate.class);
                updateYlhAdspotSdkChannel(ylhFeedCreate, sdkChannel, originSdkChannel);
                break;
            case CONST.EASYADS_INCENTIVE:
                YlhIncentiveCreate ylhIncentiveCreate = JSONObject.parseObject(requestBody).getObject("auto_adspot", YlhIncentiveCreate.class);
                updateYlhAdspotSdkChannel(ylhIncentiveCreate, sdkChannel, originSdkChannel);
                break;
        }
    }

    public void updateYlhAdspotSdkChannel(YlhCreateBase ylhEcpmUpdate, SdkChannel sdkChannel, SdkChannel originSdkChannel) throws Exception {
        // 因为优量汇更新cpm 的接口，cpm 是必填的。但是前端更新接口的时候，可能就是保持 不设置cpm ，此时cpm 没有值，在优量汇哪里就会报错，因此判断一下，如果cpm 为空，就直接不走优量汇更新cpm那个接口
        if (ylhEcpmUpdate.getEcpmPrice() != null) {
            // 如果用户选择了关闭报表API + 自动创建广告源的账户
            if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
                sdkChannel.setSupplierAdspotConfig(CsjJsonUtils.toJson(ylhEcpmUpdate));
            } else {
                long placementId = Long.parseLong(sdkChannel.getParams().getAdspotId());
                long memberId = parseMemberId(sdkChannel);
                String secret = sdkChannel.getReportApiParam().getChannelParams().get("secret");

                if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
                    throw new AutoAdspotException("账户名称下未配置相关参数");
                }

                ylhEcpmUpdate.setMemberId(memberId);
                ylhEcpmUpdate.setPlacementId(placementId);

                String bodyString = CsjJsonUtils.toJson(ylhEcpmUpdate);
                String token = YlhSignature.calcToken(memberId, secret);
                String responseJson = send_request(CONST.YLH_URL_UPDATE, bodyString, token);

                JsonNode response = JsonUtils.getJsonNode(responseJson);
                int responseCode = response.path("code").asInt();

                if (responseCode != 0) {
                    throw new AutoAdspotException("广告平台配置信息错误，请确认当前配置信息是否填写有误；若无权限请与三方广告平台沟通并开通权限。");
                }

                sdkChannel.setSupplierAdspotConfig(bodyString);
            }

            // 从10 =》 10，不应该认为是更新，优量汇平台也不认为，只有他们不相等的时候，才更新cpm 的更新时间。 优量汇也是
            if (ylhEcpmUpdate.getPriceStrategyType().equals("TargetPrice")) {
                YlhCreateBase originYlhAutoAdspot = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), YlhCreateBase.class);
                if (!ylhEcpmUpdate.getEcpmPrice().equals(originYlhAutoAdspot.getEcpmPrice())) {
                    sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                }
            }
        } else {
            // 当是eCpm 为空的时候，不走eCpm 更新的接口，直接手动把数据库中取的值，再塞回去就好了。这里一定要有这个set的过程，否则 updateOneAdspotSdkChannel 的时候，sdkChannel 里面的 supplierAdspotConfig 是空
            sdkChannel.setSupplierAdspotConfig(originSdkChannel.getSupplierAdspotConfig());
        }
    }
}
