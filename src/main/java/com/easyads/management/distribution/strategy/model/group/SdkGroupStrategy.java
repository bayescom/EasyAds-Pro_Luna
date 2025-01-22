package com.easyads.management.distribution.strategy.model.group;

import com.easyads.component.utils.DataStringUtils;
import com.easyads.management.common.Direction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SdkGroupStrategy {
    private Long groupTargetId;
    private String name;
    private int priority;
    private Map<String, Direction> direction;
    private SdkGroupDirectionOrigin sdkGroupDirectionOrigin;

    public SdkGroupStrategy() {
        this.groupTargetId = null;
        this.name = "默认分组";
        this.priority = 1;
        this.sdkGroupDirectionOrigin = new SdkGroupDirectionOrigin(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    public Long getGroupTargetId() {
        return groupTargetId;
    }

    public void setGroupTargetId(Long groupTargetId) {
        this.groupTargetId = groupTargetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsonIgnore
    public SdkGroupDirectionOrigin getSdkGroupDirectionOrigin() {
        return sdkGroupDirectionOrigin;
    }

    public void setSdkGroupDirectionOrigin(SdkGroupDirectionOrigin sdkGroupDirectionOrigin) {
        this.sdkGroupDirectionOrigin = sdkGroupDirectionOrigin;
    }

    public Map<String, Direction> getDirection() {
        this.direction = new HashMap<>();

        // App版本
        String app_version = this.sdkGroupDirectionOrigin.getAppVersion();
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

        // SDK版本
        String sdk_version = this.sdkGroupDirectionOrigin.getSdkVersion();
        if(StringUtils.isNotBlank(sdk_version)) {
            String property = "include";
            if(sdk_version.startsWith(">=")) {
                sdk_version = sdk_version.replace(">=", StringUtils.EMPTY);
                property = ">=";
            } else if(sdk_version.startsWith("<=")) {
                sdk_version = sdk_version.replace("<=", StringUtils.EMPTY);
                property = "<=";
            } else if(sdk_version.startsWith("!")) {
                sdk_version = sdk_version.replace("!", StringUtils.EMPTY);
                property = "exclude";
            }
            this.direction.put("sdkVersion", new Direction(property, DataStringUtils.stringExplodeList(sdk_version, ",")));
        } else {
            this.direction.put("sdkVersion", new Direction("", new ArrayList<>()));
        }

        return this.direction;
    }

    // 清理id信息，这个功能主要用在copy分组的时候，清理掉id信息
    public void clearIdInfo() {
        this.groupTargetId = null;
//
//        for(DimensionTarget dt : this.dimensionTargetList) {
//            dt.setModelId(null);
//        }
    }

    public void completeDbBean() {
        /*
         * 1. 定向信息转成数据库可写信息
         */
        this.sdkGroupDirectionOrigin = new SdkGroupDirectionOrigin(StringUtils.EMPTY, StringUtils.EMPTY);

        // App版本定向
        String property = this.direction.get("appVersion").getProperty();
        if("include".equals(property)) {
            this.sdkGroupDirectionOrigin.setAppVersion(StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else if("exclude".equals(property)) {
            this.sdkGroupDirectionOrigin.setAppVersion("!" + StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else if(">=".equals(property)) {
            this.sdkGroupDirectionOrigin.setAppVersion(">=" + StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else if("<=".equals(property)) {
            this.sdkGroupDirectionOrigin.setAppVersion("<=" + StringUtils.join(this.direction.get("appVersion").getValue(), ","));
        } else {
            this.sdkGroupDirectionOrigin.setAppVersion("");
        }

        // SDK版本定向
        property = this.direction.get("sdkVersion").getProperty();
        if("include".equals(property)) {
            this.sdkGroupDirectionOrigin.setSdkVersion(StringUtils.join(this.direction.get("sdkVersion").getValue(), ","));
        } else if("exclude".equals(property)) {
            this.sdkGroupDirectionOrigin.setSdkVersion("!" + StringUtils.join(this.direction.get("sdkVersion").getValue(), ","));
        } else if(">=".equals(property)) {
            this.sdkGroupDirectionOrigin.setSdkVersion(">=" + StringUtils.join(this.direction.get("sdkVersion").getValue(), ","));
        } else if("<=".equals(property)) {
            this.sdkGroupDirectionOrigin.setSdkVersion("<=" + StringUtils.join(this.direction.get("sdkVersion").getValue(), ","));
        } else {
            this.sdkGroupDirectionOrigin.setSdkVersion("");
        }
    }
}
