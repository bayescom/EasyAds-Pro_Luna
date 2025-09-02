package com.easyads.management.distribution.sdk.service;

import com.alibaba.fastjson.JSONObject;

import com.easyads.component.CONST;
import com.easyads.component.exception.AutoAdspotException;
import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.component.utils.CsjJsonUtils;
import com.easyads.component.utils.JsonUtils;
import com.easyads.component.utils.TimeUtils;
import com.easyads.management.distribution.auto_adspot.bd_platform.model.BdCpmUpdate;
import com.easyads.management.distribution.auto_adspot.bd_platform.model.BdCreateBase;
import com.easyads.management.distribution.auto_adspot.bd_platform.model.BdQuery;
import com.easyads.management.distribution.auto_adspot.bd_platform.model.BdUbapiClient;
import com.easyads.management.distribution.sdk.model.SdkChannel;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;

@Service
public class BdAutoAdspotService {
    @Autowired
    private SdkChannelMapper sdkChannelMapper;

    private BdUbapiClient createUbapiClient(String accessKey, String privateKey) throws Exception {
        BdUbapiClient client = new BdUbapiClient(CONST.BD_UBAPI_HOST, accessKey, privateKey);
        return client;
    }

    private String getRequestBodyString(String requestBody, SdkChannel sdkChannel, Integer adspotType) {
        // adspotType /** 1 - 开屏， 2 信息流， 3 横幅， 4 插屏， 5 激励视频 */
        String appId = sdkChannel.getParams().getMediaId();

        String bodyString;

        BdCreateBase bdCreateBase = JSONObject.parseObject(requestBody).getObject("auto_adspot", BdCreateBase.class);
        bdCreateBase.setAppSid(appId);
        bdCreateBase.setAdType(adspotType);
        bodyString = CsjJsonUtils.toJson(bdCreateBase);
        return bodyString;
    }

    private Boolean cpmIsUnchanged(BdCpmUpdate bdCpmUpdate, SdkChannel sdkChannel, SdkChannel originSdkChannel, Integer adspotType) {
        Boolean IsUnchanged = false;
        BdCreateBase bdCreateBase = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), BdCreateBase.class);
        if (bdCpmUpdate.getCpm().equals(bdCreateBase.getCpm())) {
            IsUnchanged = true;
        }
        return IsUnchanged;
    }

    public JsonNode getOneBdAdspotSdkChannel(SdkChannel sdkChannel, Integer sdkChannelId, Long adspotId) throws Exception {
        // 会遇到先创建出来的广告源，然后再在广告网络中将他对应的账户给删掉，或者在广告网络中将对应的账户的【自动创建广告源】按钮给关闭（dkChannel.getReportApiParam().getAutoCreateStatus() == 0），的情况，在获取签名的时候会报错
        // 产品需求：有账户，可查询 更改 发送请求到百度 无账户 使用数据库存储数据
        if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
            JsonNode autoAdspot = JsonUtils.getJsonNode(sdkChannel.getSupplierAdspotConfig());
            return autoAdspot;
        } else {
            // 这里是通过接口拿到最新的数据，
            String accessKey = sdkChannel.getReportApiParam().getChannelParams().get("access_key");
            String privateKey = sdkChannel.getReportApiParam().getChannelParams().get("private_key");
            Long bdAdspotId = Long.parseLong(sdkChannel.getParams().getAdspotId());

            BdUbapiClient client = createUbapiClient(accessKey, privateKey);

            BdQuery bdQuery = new BdQuery();
            bdQuery.setTuIds(Arrays.asList(bdAdspotId));

            String body = CsjJsonUtils.toJson(bdQuery);
            HttpResponse response = client.post(CONST.BD_QUERY_URI, "application/json", body.getBytes());
            String responseString =  EntityUtils.toString(response.getEntity());
            JsonNode responseJsonNode = JsonUtils.getJsonNode(responseString);

            int code = responseJsonNode.path("code").asInt();

            if (code != 0) {
                String message = responseJsonNode.path("message").asText();
                throw new AutoAdspotException(message);
            }
            JsonNode responseData = responseJsonNode.path("data");
            JsonNode innerData = responseData.path("data");
            JsonNode firstAdspot = innerData.get(0);

            // 每次get的时候，都拿到最新的广告源，塞到数据库中，用来进行更新的时候对cpm的对比
            sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(firstAdspot));
            sdkChannelMapper.updateOneAdspotSdkChannelSupplierAdspotConfig(adspotId, sdkChannelId, sdkChannel);
            return firstAdspot;
        }
    }


    public void createOneBdAdspotSdkChannel(SdkChannel sdkChannel, Integer adspotType,String requestBody) throws Exception {
        String accessKey = sdkChannel.getReportApiParam().getChannelParams().get("access_key");
        String privateKey = sdkChannel.getReportApiParam().getChannelParams().get("private_key");
        BdUbapiClient client = createUbapiClient(accessKey, privateKey);

        String bodyString = getRequestBodyString(requestBody, sdkChannel, adspotType);
        HttpResponse response = client.post(CONST.BD_CREATE_URI, "application/json", bodyString.getBytes());
        String responseString = EntityUtils.toString(response.getEntity());
        JsonNode responseJsonNode = JsonUtils.getJsonNode(responseString);

        int code = responseJsonNode.path("code").asInt();
        if (code != 0) {
            String message = responseJsonNode.path("message").asText();
            throw new AutoAdspotException(message);
        }

        JsonNode responseData = responseJsonNode.path("data");

        // 将他存到数据库中
        BdCreateBase bdCreateBase = JSONObject.parseObject(requestBody).getObject("auto_adspot", BdCreateBase.class);
        sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(bdCreateBase));

        sdkChannel.getParams().setAdspotId(responseData.path("tu_id").asText());
        // 百度新建的时候也算是cpm 的更新时间
        sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
    }


    public void updateOneBdAdspotSdkChannel(SdkChannel sdkChannel, SdkChannel originSdkChannel,  String requestBody, int adspotType) throws Exception {
        // 因为百度更新cpm 的接口，cpm 是必填的。但是前端更新接口的时候，可能就是保持 不设置cpm ，此时cpm 没有值，在百度哪里就会报错，因此判断一下，如果cpm 为空，就直接不走百度更新cpm那个接口
        BdCpmUpdate bdCpmUpdate = JSONObject.parseObject(requestBody).getObject("auto_adspot", BdCpmUpdate.class);

        // 如果当前账户为空，那么取数据库中的
        if (sdkChannel.getReportApiParam().getChannelParams().isEmpty()) {
            sdkChannel.setSupplierAdspotConfig(originSdkChannel.getSupplierAdspotConfig());
        } else {
            // 百度这里，从10- 10 ，是会报错的，因此，需要判断一下，如果值不变的话，就不要走编辑接口了
            Boolean cpmIsUnchanged = cpmIsUnchanged(bdCpmUpdate, sdkChannel, originSdkChannel, adspotType);
            if (bdCpmUpdate.getCpm() != null && !cpmIsUnchanged) {
                String accessKey = sdkChannel.getReportApiParam().getChannelParams().get("access_key");
                String privateKey = sdkChannel.getReportApiParam().getChannelParams().get("private_key");
                Long adspotId = Long.parseLong(sdkChannel.getParams().getAdspotId());

                bdCpmUpdate.setTuId(adspotId);

                BdUbapiClient client = createUbapiClient(accessKey, privateKey);
                String bodyString = CsjJsonUtils.toJson(bdCpmUpdate);
                HttpResponse response = client.post(CONST.BD_UPDATE_URI, "application/json", bodyString.getBytes());

                String responseString = EntityUtils.toString(response.getEntity());
                JsonNode responseJsonNode = JsonUtils.getJsonNode(responseString);

                int code = responseJsonNode.path("code").asInt();
                if (code != 0) {
                    String message = responseJsonNode.path("message").asText();
                    throw new AutoAdspotException(message);
                }

                BdCreateBase bdCreateBase = JSONObject.parseObject(originSdkChannel.getSupplierAdspotConfig(), BdCreateBase.class);
                sdkChannel.setCpmUpdateTime(TimeUtils.getCurrentTimestamp());
                bdCreateBase.setCpm(bdCpmUpdate.getCpm());
                sdkChannel.setSupplierAdspotConfig(JSONObject.toJSONString(bdCreateBase));
            } else {
                // 当是 cpm 为空的时候，不走 cpm 更新的接口，直接手动把数据库中取的值，再塞回去就好了。这里一定要有这个set的过程，否则 updateOneAdspotSdkChannel 的时候，sdkChannel 里面的 supplierAdspotConfig 是空
                sdkChannel.setSupplierAdspotConfig(originSdkChannel.getSupplierAdspotConfig());
            }
        }
    }
}
