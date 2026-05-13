package com.easyads.management.distribution.sdk.model;

import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.DirectionUtils;
import com.easyads.component.utils.JsonUtils;
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
    private int adspotId;
    private String adspotName;
    private int adspotType;
    private SdkGroup sdkGroup;
    @JsonIgnore
    private int sdkChannelId;
    private String sdkChannelName;
    private String sdkChannelAlias;
    private Float price;
    private int status;
    private SdkParams sdkChannelParams;
    private SdkConfig sdkConfig;
    @Setter
    private SdkData data;
    private List<ChannelTrafficDirectionReveal> directionList;
    private List<LimitReveal> limitList;
    private Integer isAutoCreate;

    public SdkChannelTrafficSummary(long id,
                                    int platform_type,
                                    int media_id,
                                    String media_name,
                                    int adspot_id,
                                    String adspot_name,
                                    int adspot_type,
                                    String group_tag,
                                    long sdk_group_id,
                                    long sdk_group_percentage_id,
                                    long sdk_group_targeting_id,
                                    long sdk_group_targeting_percentage_id,
                                    int supplier_id,
                                    String sdk_channel_name,
                                    String sdk_channel_alias,
                                    int status,
                                    int is_auto_create,
                                    String supplier_params,
                                    Integer is_head_bidding,
                                    Float bid_ratio,
                                    String location_list,
                                    String make_list,
                                    String osv_list,
                                    String app_versions,
                                    int daily_req_limit,
                                    int daily_imp_limit,
                                    int device_daily_req_limit,
                                    int device_daily_imp_limit,
                                    int device_request_interval) {
        this.id = id;
        this.platformType = platform_type;
        this.mediaId = media_id;
        this.mediaName = media_name;
        this.adspotId = adspot_id;
        this.adspotName = adspot_name;
        this.adspotType = adspot_type;
        this.sdkGroup = new SdkGroup(group_tag, sdk_group_id, sdk_group_percentage_id, sdk_group_targeting_id, sdk_group_targeting_percentage_id);
        this.sdkChannelId = supplier_id;
        this.sdkChannelName = sdk_channel_name;
        this.sdkChannelAlias = sdk_channel_alias;
        this.status = status;
        this.isAutoCreate = is_auto_create;
        this.sdkChannelParams = JsonUtils.convertJsonToObject(supplier_params, SdkParams.class);
        this.sdkConfig = new SdkConfig(is_head_bidding, bid_ratio);
        this.directionList = genDirectionList(location_list, make_list, app_versions, osv_list);
        this.limitList = genLimitList(daily_req_limit, daily_imp_limit, device_daily_req_limit,
                device_daily_imp_limit, device_request_interval);
    }

    private List<ChannelTrafficDirectionReveal> genDirectionList(String location_list, String make_list, String app_version, String osv_list) {
        List<ChannelTrafficDirectionReveal> directionList = new ArrayList<>();

        // 地域的省市定向
        if(StringUtils.isNotBlank(location_list)) {
            List<String> locationIdList = new ArrayList<>();
            locationIdList.addAll(DataStringUtils.stringExplodeList(location_list, ","));
            directionList.add(new ChannelTrafficDirectionReveal("地域", "包含", DirectionUtils.locationIdList2Name(locationIdList)));
        }

        // 制造商定向
        if(StringUtils.isNotBlank(make_list)) {
            directionList.add(new ChannelTrafficDirectionReveal("制造商", "包含", DirectionUtils.makeList2Name(DataStringUtils.stringExplodeList(make_list, ","))));
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
        }

        return directionList;
    }

    private List<LimitReveal> genLimitList(int daily_req_limit, int daily_imp_limit,
                                           int device_daily_req_limit, int device_daily_imp_limit, int device_request_interval) {
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

        if(device_request_interval > 0) {
            limitList.add(new LimitReveal("设备请求间隔", device_request_interval));
        }

        return limitList;
    }
}
