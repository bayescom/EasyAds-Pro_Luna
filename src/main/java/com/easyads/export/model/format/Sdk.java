package com.easyads.export.model.format;

import com.easyads.component.utils.JsonUtils;
import com.easyads.export.model.origin.SdkOriginInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class Sdk {
    private String sdk_id;
    private String id;
    private String supplier_id;
    private String supplier_key;
    private String priority;
    private String index;
    private String name;
    private String adspotid;
    private String appid;
    private String app_key;
    private String app_secret;
    private String timeout;
    private String sdk_tag;
    private int is_head_bidding;
    private int sdk_price;
    private float bid_ratio;
    private Map<String, InExCludeConf> system_direct;
    private SdkRequestLimit request_limit;

    public Sdk(SdkOriginInfo sdkOriginInfo) {
        this.sdk_id = sdkOriginInfo.getId(); // adspot_sdk在数据库里面的id
        this.id = sdkOriginInfo.getAdn_id(); // adn_id
        this.supplier_id = sdkOriginInfo.getAdn_id(); // adn_id
        this.priority = "0";
        this.index = "0";
        this.name = sdkOriginInfo.getName();
        genSupplierParams(sdkOriginInfo.getSupplier_params());
        this.timeout = String.valueOf(sdkOriginInfo.getTime_out());
        this.is_head_bidding = sdkOriginInfo.getIs_head_bidding();
        this.sdk_price = Math.round(Float.valueOf(sdkOriginInfo.getBid_price()) * 100);
        this.bid_ratio = sdkOriginInfo.getBid_ratio();
        this.system_direct = genSystemDirect(sdkOriginInfo);
        this.request_limit = new SdkRequestLimit(sdkOriginInfo);
    }

    private void genSupplierParams(String supplierParams) {
        if (StringUtils.isBlank(supplierParams)) {
            return;
        }
        try {
            JsonNode jsonNode = JsonUtils.getJsonNode(supplierParams);
            this.adspotid = jsonNode.has("adspot_id") ? jsonNode.get("adspot_id").asText() : null;
            this.appid = jsonNode.has("app_id") ? jsonNode.get("app_id").asText() : null;
            this.app_key = jsonNode.has("app_key") ? jsonNode.get("app_key").asText() : null;
            this.app_secret = jsonNode.has("app_secret") ? jsonNode.get("app_secret").asText() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, InExCludeConf> genSystemDirect(SdkOriginInfo sdkOriginInfo) {
        Map<String, InExCludeConf> stringInExCludeConfMap = new HashMap<>();

        // location
        String locationList = sdkOriginInfo.getLocation_list();
        if(StringUtils.isNotBlank(locationList)) {
            if(locationList.startsWith("!")) {
                stringInExCludeConfMap.put("location", new InExCludeConf(new ArrayList<>(), Arrays.asList(locationList.replace("!", "").split(","))));
            } else {
                stringInExCludeConfMap.put("location", new InExCludeConf(Arrays.asList(locationList.split(",")), new ArrayList<>()));
            }
        }

        // make
        String makeList = sdkOriginInfo.getMake_list();
        if(StringUtils.isNotBlank(makeList)) {
            if(makeList.startsWith("!")) {
                stringInExCludeConfMap.put("make", new InExCludeConf(new ArrayList<>(), Arrays.asList(makeList.replace("!", "").split(","))));
            } else {
                stringInExCludeConfMap.put("make", new InExCludeConf(Arrays.asList(makeList.split(",")), new ArrayList<>()));
            }
        }

        // osv
        String osvList = sdkOriginInfo.getOsv_list();
        if(StringUtils.isNotBlank(osvList)) {
            if(osvList.startsWith("!")) {
                stringInExCludeConfMap.put("osv", new InExCludeConf(new ArrayList<>(), Arrays.asList(osvList.replace("!", "").split(","))));
            } else {
                stringInExCludeConfMap.put("osv", new InExCludeConf(Arrays.asList(osvList.split(",")), new ArrayList<>()));
            }
        }

        // app_version
        String versionInfo = sdkOriginInfo.getApp_versions();
        if(StringUtils.isNotBlank(versionInfo)) {
            if (versionInfo.startsWith(">=")) {
                stringInExCludeConfMap.put("app_version",
                        new InExCludeConf(versionInfo.replace(">=", ""), StringUtils.EMPTY, new ArrayList<>(), new ArrayList<>()));
            } else if (versionInfo.startsWith("<=")) {
                stringInExCludeConfMap.put("app_version",
                        new InExCludeConf(StringUtils.EMPTY, versionInfo.replace("<=", ""), new ArrayList<>(), new ArrayList<>()));
            } else if (versionInfo.startsWith("!")) {
                stringInExCludeConfMap.put("app_version",
                        new InExCludeConf(new ArrayList<>(), Arrays.asList(versionInfo.replace("!", "").split(","))));
            } else {
                stringInExCludeConfMap.put("app_version",
                        new InExCludeConf(Arrays.asList(versionInfo.split(",")), new ArrayList<>()));
            }
        }

        return stringInExCludeConfMap;
    }
}
