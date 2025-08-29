package com.easyads.management.distribution.sdk.model;


import com.easyads.component.utils.DataStringUtils;
import com.easyads.management.adn.model.bean.ParamMeta;
import com.easyads.management.adn.model.bean.SdkAdnReportApi;
import com.easyads.management.common.Direction;
import com.easyads.management.adn.model.data.SdkData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@NoArgsConstructor
public class SdkChannel {
    private Integer id;
    private Integer adnId;
    @JsonIgnore
    private Integer reportChannelId; // 用于媒体报表的识别，不同于channelId
    private String channelName;
    private String channelAlias;
    private Float bidPrice;
    private Integer isHeadBidding;
    private Float bidRatio;
    private int timeout;
    private List<ParamMeta> adnParamsMeta;
    private Map<String, Direction> direction;
    private SdkRequestLimit requestLimit;
    @JsonIgnore
    private SdkDirectionOrigin directionOrigin;
    private SdkParams params;
    private SdkData data;
    private SdkAdnReportApi reportApiParam;
    // 因为自动创建三方广告位新增如下字段
    // 是否自动创建的
    private Integer isAutoCreate = 0;
    private Long cpmUpdateTime;
    private String supplierAdspotConfig = null; // 直接存储JSON字符串

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdnId() {
        return adnId;
    }

    public void setAdnId(Integer adnId) {
        this.adnId = adnId;
    }

    public Integer getReportChannelId() {
        return reportChannelId;
    }

    public void setReportChannelId(Integer reportChannelId) {
        this.reportChannelId = reportChannelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelAlias() {
        return channelAlias;
    }

    public void setChannelAlias(String channelAlias) {
        this.channelAlias = channelAlias;
    }

    public Float getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Float bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Integer getIsHeadBidding() {
        return isHeadBidding;
    }

    public void setIsHeadBidding(Integer isHeadBidding) {
        this.isHeadBidding = isHeadBidding;
    }

    public Float getBidRatio() {
        return bidRatio;
    }

    public void setBidRatio(Float bidRatio) {
        this.bidRatio = bidRatio;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<ParamMeta> getAdnParamsMeta() {
        return adnParamsMeta;
    }

    public void setAdnParamsMeta(List<ParamMeta> adnParamsMeta) {
        this.adnParamsMeta = adnParamsMeta;
    }

    public Map<String, Direction> getDirection() {
        this.direction = new HashMap<>();

        // 地域的省市定向
        String location_list = this.directionOrigin.getLocationList();
        if(StringUtils.isNotBlank(location_list)) {
            String property = "include";
            if(location_list.startsWith("!")) {
                location_list = location_list.replace("!", StringUtils.EMPTY);
                property = "exclude";
            }
            this.direction.put("location", new Direction(property, DataStringUtils.stringExplodeList(location_list, ",")));
        } else {
            this.direction.put("location", new Direction("", new ArrayList<>()));
        }

        // 制造商定向
        String make_list = this.directionOrigin.getMakeList();
        if(StringUtils.isNotBlank(make_list)) {
            String property = "include";
            if(make_list.startsWith("!")) {
                make_list = make_list.replace("!", StringUtils.EMPTY);
                property = "exclude";
            }
            this.direction.put("deviceMaker", new Direction(property, DataStringUtils.stringExplodeList(make_list, ",")));
        } else {
            this.direction.put("deviceMaker", new Direction("", new ArrayList<>()));
        }

        // 系统版本定向
        String osv_list = this.directionOrigin.getOsvList();
        if(StringUtils.isNotBlank(osv_list)) {
            String property = "include";
            if (osv_list.startsWith("!")) {
                osv_list = osv_list.replace("!", StringUtils.EMPTY);
                property = "exclude";
            }
            this.direction.put("osVersion", new Direction(property, DataStringUtils.stringExplodeList(osv_list, ",")));
        } else {
            this.direction.put("osVersion", new Direction("", new ArrayList<>()));
        }

        // App版本
        String app_version = this.directionOrigin.getAppVersion();
        if(StringUtils.isNotBlank(app_version)) {
            String property = "include";
            if(app_version.startsWith(">=")) {
                app_version = app_version.replace(">=", StringUtils.EMPTY);
                property = ">=";
            } else if(app_version.startsWith("<=")) {
                app_version = app_version.replace("<=", StringUtils.EMPTY);
                property = "<=";
            } else if(app_version.startsWith("!")) {
                app_version = app_version.replace("!", StringUtils.EMPTY);
                property = "exclude";
            }
            this.direction.put("appVersion", new Direction(property, DataStringUtils.stringExplodeList(app_version, ",")));
        } else {
            this.direction.put("appVersion", new Direction("", new ArrayList<>()));
        }

        return this.direction;
    }

    public SdkRequestLimit getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(SdkRequestLimit requestLimit) {
        this.requestLimit = requestLimit;
    }

    public SdkParams getParams() {
        return params;
    }

    public void setParams(SdkParams params) {
        this.params = params;
    }

    public SdkDirectionOrigin getDirectionOrigin() {
        return directionOrigin;
    }

    public void setDirectionOrigin(SdkDirectionOrigin directionOrigin) {
        this.directionOrigin = directionOrigin;
    }

    public SdkData getData() {
        return data;
    }

    public void setData(SdkData data) {
        this.data = data;
    }

    public SdkAdnReportApi getReportApiParam() {
        return reportApiParam;
    }

    public void setReportApiParam(SdkAdnReportApi reportApiParam) {
        this.reportApiParam = reportApiParam;
    }

    public Integer getIsAutoCreate() {
        return isAutoCreate;
    }

    public void setIsAutoCreate(Integer isAutoCreate) {
        this.isAutoCreate = isAutoCreate;
    }

    public Long getCpmUpdateTime() {
        return cpmUpdateTime;
    }

    public void setCpmUpdateTime(Long cpmUpdateTime) {
        this.cpmUpdateTime = cpmUpdateTime;
    }

    @JsonIgnore
    public String getSupplierAdspotConfig() {
        return supplierAdspotConfig;
    }

    public void setSupplierAdspotConfig(String supplierAdspotConfig) {
        this.supplierAdspotConfig = supplierAdspotConfig;
    }

    public void completeDbBean() {
        /*
         * 1. 定向信息转成数据库可写信息
         */
        this.directionOrigin = new SdkDirectionOrigin();

        // 地域定向
        if(this.direction.get("location").getProperty().equals("include")) {
            this.directionOrigin.setLocationList(StringUtils.join(this.direction.get("location").getValue(), ","));
        } else {
            // exclude前面加上!
            this.directionOrigin.setLocationList("!" + StringUtils.join(this.direction.get("location").getValue(), ","));
        }

        // 设备定向
        if(this.direction.get("deviceMaker").getProperty().equals("include")) {
            this.directionOrigin.setMakeList(StringUtils.join(this.direction.get("deviceMaker").getValue(), ","));
        } else {
            // exclude前面加上!
            this.directionOrigin.setMakeList("!" + StringUtils.join(this.direction.get("deviceMaker").getValue(), ","));
        }

        // App版本定向
        String property = this.direction.get("appVersion").getProperty();
        if("include".equals(property)) {
            this.directionOrigin.setAppVersion(StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else if("exclude".equals(property)) {
            this.directionOrigin.setAppVersion("!" + StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else if(">=".equals(property)) {
            this.directionOrigin.setAppVersion(">=" + StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else if("<=".equals(property)) {
            this.directionOrigin.setAppVersion("<=" + StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else {
            this.directionOrigin.setAppVersion("");
        }

        // 操作系统版本定向
        if(this.direction.get("osVersion").getProperty().equals("include")) {
            this.directionOrigin.setOsvList(StringUtils.join(this.direction.get("osVersion").getValue(), ","));
        } else {
            // exclude前面加上!
            this.directionOrigin.setOsvList("!" + StringUtils.join(this.direction.get("osVersion").getValue(), ","));
        }
    }
}
