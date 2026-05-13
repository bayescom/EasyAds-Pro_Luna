package com.easyads.management.distribution.sdk.model;

import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.DirectionUtils;
import com.easyads.management.adn.model.data.SdkData;
import com.easyads.management.common.LimitReveal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SdkChannelTrafficSummary {
    private Long id;
    private int platformType;
    private int mediaId;
    private String mediaName;
    private String mediaIcon;
    private int adspotId;
    private String adspotName;
    private int adspotType;
    private SdkGroup sdkGroup;
    @JsonIgnore
    private Integer reportChannelId; // 用于媒体报表的识别，不同于channelId
    private int sdkChannelId;
    private String sdkChannelName;
    private String sdkChannelAlias;
    private String sdkChannelIcon;
    private Float price;
    private int status;
    private Map<String, String> sdkChannelParams;
    private List<ChannelConfig> sdkChannelConfigs;
    private SdkConfig sdkConfig;
    @Setter
    private SdkData data;
    private List<ChannelTrafficDirectionReveal> directionList;
    private List<LimitReveal> limitList;
    private Integer isAutoCreate;

    public SdkChannelTrafficSummary(long id,
                                    int platform_type, int media_id, String media_name, String media_icon,
                                    int adspot_id, String adspot_name, int adspot_type,
                                    String group_tag, long sdk_group_id, long sdk_group_percentage_id, long sdk_group_targeting_id, long sdk_group_targeting_percentage_id,
                                    int report_channel_id, int supplier_id, String sdk_channel_name, String sdk_channel_alias, String sdk_channel_icon,
                                    Float price, int status, int is_auto_create,
                                    String supplier_app_id, String supplier_adspot_id, String supplier_app_key, String supplier_app_secret,
                                    int meta_app_id, String meta_app_id_name, int meta_app_key, String meta_app_key_name,
                                    int meta_adspot_id, String meta_adspot_id_name, int meta_pub_id, String meta_pub_id_name,
                                    int meta_pub_domain, String meta_pub_domain_name, int meta_loid, String meta_loid_name,
                                    Long layer_id, Integer is_head_bidding, Float bid_ratio, Integer enable_cache, Integer cache_timeout,
                                    String ip_white_list, String after_direct, String province_list, String exclude_province_list,
                                    String location_list, String exclude_location_list, String make_list, String make_black_list,
                                    String osv_list, String osv_black_list, String app_versions, String customer_key, Integer device_package_operator, String device_package_list,
                                    int daily_req_limit, int daily_imp_limit, int device_daily_req_limit, int device_daily_imp_limit, int device_daily_click_limit, int device_request_interval) {

        this.id = id;
        this.platformType = platform_type;
        this.mediaId = media_id;
        this.mediaName = media_name;
        this.mediaIcon = media_icon;
        this.adspotId = adspot_id;
        this.adspotName = adspot_name;
        this.adspotType = adspot_type;
        this.sdkGroup = new SdkGroup(group_tag, sdk_group_id, sdk_group_percentage_id, sdk_group_targeting_id, sdk_group_targeting_percentage_id);
        this.reportChannelId = report_channel_id;
        this.sdkChannelId = supplier_id;
        this.sdkChannelName = sdk_channel_name;
        this.sdkChannelAlias = sdk_channel_alias;
        this.sdkChannelIcon = sdk_channel_icon;
        this.price = price;
        this.status = status;
        this.isAutoCreate = is_auto_create;
        this.sdkChannelParams = genSdkChannelParams(supplier_app_id, supplier_adspot_id, supplier_app_key, supplier_app_secret);
        this.sdkChannelConfigs = genSdkChannelConfigs(meta_app_id, meta_app_id_name, meta_app_key, meta_app_key_name,
                meta_adspot_id, meta_adspot_id_name, meta_pub_id, meta_pub_id_name,
                meta_pub_domain, meta_pub_domain_name, meta_loid, meta_loid_name);
        this.sdkConfig = new SdkConfig(layer_id, is_head_bidding, bid_ratio, enable_cache, cache_timeout);
        this.directionList = genDirectionList(ip_white_list, after_direct, location_list, exclude_location_list,
                province_list, exclude_province_list, make_list, make_black_list,
                app_versions, osv_list, osv_black_list,
                customer_key, device_package_operator, device_package_list);
        this.limitList = genLimitList(daily_req_limit, daily_imp_limit, device_daily_req_limit,
                device_daily_imp_limit, device_daily_click_limit, device_request_interval);
    }

    private Map genSdkChannelParams(String supplier_app_id, String supplier_adspot_id, String supplier_app_key, String supplier_app_secret) {
        Map<String, String> channelParams = new HashMap<>();
        if(StringUtils.isNotBlank(supplier_app_id)) {
            channelParams.put("meta_app_id", supplier_app_id);
        }

        if(StringUtils.isNotBlank(supplier_adspot_id)) {
            channelParams.put("meta_adspot_id", supplier_adspot_id);
        }

        if(StringUtils.isNotBlank(supplier_app_key)) {
            channelParams.put("meta_app_key", supplier_app_key);
        }

        if(StringUtils.isNotBlank(supplier_app_secret)) {
            channelParams.put("meta_pub_id", supplier_app_secret);
        }

        return channelParams;
    }

    private String getChannelConfigKey(String key, String defaultKey) {
        if(StringUtils.isNotBlank(key)) {
            return key;
        }
        return defaultKey;
    }

    private List<ChannelConfig> genSdkChannelConfigs(int meta_app_id, String meta_app_id_name, int meta_app_key, String meta_app_key_name,
                                                     int meta_adspot_id, String meta_adspot_id_name, int meta_pub_id, String meta_pub_id_name,
                                                     int meta_pub_domain, String meta_pub_domain_name, int meta_loid, String meta_loid_name) {
        List<ChannelConfig> channelConfigs = new ArrayList<>();

        // meta_app_id
        if(1 == meta_app_id) {
            String name = getChannelConfigKey(meta_app_id_name, "meta_app_id");
            channelConfigs.add(new ChannelConfig("meta_app_id", name));
        }

        // meta_app_key
        if(1 == meta_app_key) {
            String name = getChannelConfigKey(meta_app_key_name, "meta_app_key");
            channelConfigs.add(new ChannelConfig("meta_app_key", name));
        }

        // meta_adspot_id
        if(1 == meta_adspot_id) {
            String name = getChannelConfigKey(meta_adspot_id_name, "meta_adspot_id");
            channelConfigs.add(new ChannelConfig("meta_adspot_id", name));
        }

        // meta_pub_id
        if(1 == meta_pub_id) {
            String name = getChannelConfigKey(meta_pub_id_name, "meta_pub_id");
            channelConfigs.add(new ChannelConfig("meta_pub_id", name));
        }

        // meta_pub_domain
        if(1 == meta_pub_domain) {
            String name = getChannelConfigKey(meta_pub_domain_name, "meta_pub_domain");
            channelConfigs.add(new ChannelConfig("meta_pub_domain", name));
        }

        // meta_loid
        if(1 == meta_loid) {
            String name = getChannelConfigKey(meta_loid_name, "meta_loid");
            channelConfigs.add(new ChannelConfig("meta_loid", name));
        }

        return channelConfigs;
    }

    private List<ChannelTrafficDirectionReveal> genDirectionList(String ip_list, String ip_config, String location_list, String location_black_list,
                                                                 String city_list, String city_black_list, String make_list, String make_black_list,
                                                                 String app_version, String osv_list, String osv_black_list,
                                                                 String customer_key, Integer device_package_operator, String device_package_list) {
        List<ChannelTrafficDirectionReveal> directionList = new ArrayList<>();

        // 地域的省市定向
        if(StringUtils.isNotBlank(location_list) || StringUtils.isNotBlank(city_list)) {
            List<String> locationIdList = new ArrayList<>();
            locationIdList.addAll(DataStringUtils.stringExplodeList(location_list, ","));
            locationIdList.addAll(DataStringUtils.stringExplodeList(city_list, ","));
            directionList.add(new ChannelTrafficDirectionReveal("地域", "包含", DirectionUtils.locationIdList2Name(locationIdList)));
        } else {
            if (StringUtils.isNotBlank(location_black_list) || StringUtils.isNotBlank(city_black_list)) {
                List<String> locationIdList = new ArrayList<>();
                locationIdList.addAll(DataStringUtils.stringExplodeList(location_black_list, ","));
                locationIdList.addAll(DataStringUtils.stringExplodeList(city_black_list, ","));
                directionList.add(new ChannelTrafficDirectionReveal("地域", "排除", DirectionUtils.locationIdList2Name(locationIdList)));
            }
        }

        // 制造商定向
        if(StringUtils.isNotBlank(make_list)) {
            directionList.add(new ChannelTrafficDirectionReveal("制造商", "包含", DirectionUtils.makeList2Name(DataStringUtils.stringExplodeList(make_list, ","))));
        } else {
            if (StringUtils.isNotBlank(make_black_list)) {
                directionList.add(new ChannelTrafficDirectionReveal("制造商", "排除", DirectionUtils.makeList2Name(DataStringUtils.stringExplodeList(make_black_list, ","))));
            }
        }

        // App版本
        if(StringUtils.isNotBlank(app_version)) {
            String property = "包含";
            if(app_version.startsWith(">=")) {
                app_version.replace(">=", StringUtils.EMPTY);
                property = "大于等于";
            } else if(app_version.startsWith("<=")) {
                app_version.replace("<=", StringUtils.EMPTY);
                property = "小于等于";
            } else if(app_version.startsWith("!")) {
                app_version.replace("!", StringUtils.EMPTY);
                property = "排除";
            }
            directionList.add(new ChannelTrafficDirectionReveal("App版本", property,
                    DataStringUtils.stringExplodeList(app_version, ",")));
        }

        // 系统版本定向
        if(StringUtils.isNotBlank(osv_list)) {
            directionList.add(new ChannelTrafficDirectionReveal("系统版本", "包含", DataStringUtils.stringExplodeList(osv_list, ",")));
        } else {
            if (StringUtils.isNotBlank(osv_black_list)) {
                directionList.add(new ChannelTrafficDirectionReveal("系统版本", "排除", DataStringUtils.stringExplodeList(osv_black_list, ",")));
            }
        }

        // 自定义定向
        if(StringUtils.isNotBlank(customer_key)) {
            directionList.add(new ChannelTrafficDirectionReveal("自定义定向", "定向key", DataStringUtils.stringExplodeList(customer_key, ",")));
        }

        // 设备包定向
        if(null != device_package_operator && StringUtils.isNotBlank(device_package_list)) {
            String property = 0 == device_package_operator ? "包含" : "排除";
            directionList.add(new ChannelTrafficDirectionReveal("设备包", property, DataStringUtils.stringExplodeList(device_package_list, ",")));
        }

        return directionList;
    }

    private List<LimitReveal> genLimitList(int daily_req_limit, int daily_imp_limit,
                                           int device_daily_req_limit, int device_daily_imp_limit, int device_daily_click_limit,
                                           int device_request_interval) {
        List<LimitReveal> limitList = new ArrayList<>();
        if(daily_req_limit > 0) {
            limitList.add(new LimitReveal("日请求", daily_req_limit));
        }

        if(daily_imp_limit > 0) {
            limitList.add(new LimitReveal("日曝光", daily_imp_limit));
        }

        if(device_daily_req_limit > 0) {
            limitList.add(new LimitReveal("设备日请求", device_daily_req_limit));
        }

        if(device_daily_imp_limit > 0) {
            limitList.add(new LimitReveal("设备日曝光", device_daily_imp_limit));
        }

        if(device_daily_click_limit > 0) {
            limitList.add(new LimitReveal("设备日点击", device_daily_click_limit));
        }

        if(device_request_interval > 0) {
            limitList.add(new LimitReveal("设备请求间隔", device_request_interval));
        }

        return limitList;
    }
}
