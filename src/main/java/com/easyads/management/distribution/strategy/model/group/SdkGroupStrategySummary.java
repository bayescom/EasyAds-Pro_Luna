package com.easyads.management.distribution.strategy.model.group;

import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.DirectionUtils;
import com.easyads.component.utils.LocationUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SdkGroupStrategySummary {

    private Long groupTargetId;
    private List<SdkStrategyDirectionReveal> directionList;


    public SdkGroupStrategySummary(Long id, String sdk_version, String app_version,
                                   String location_list, String make_list, String osv_list,
                                   String customer_key, Integer device_package_operator, String device_package_list) {
        this.groupTargetId = id;
        genDirectionList(sdk_version, app_version, location_list, make_list, osv_list, customer_key, device_package_operator, device_package_list);
    }

    public void genDirectionList(String sdk_version, String app_version, String location_list, String make_list, String osv_list,
                                 String customer_key, Integer device_package_operator, String device_package_list) {
        this.directionList = new ArrayList<>();

        // SDK版本定向
        if(StringUtils.isNotBlank(sdk_version)) {
            String property = "包含";
            if(sdk_version.startsWith(">=")) {
                property = "大于等于";
            } else if(sdk_version.startsWith("<=")) {
                property = "小于等于";
            } else if(sdk_version.startsWith("!")) {
                property = "排除";
            }
            this.directionList.add(new SdkStrategyDirectionReveal("SDK版本", property, DataStringUtils.stringExplodeList(sdk_version, ",")));
        }

        // App版本定向
        if(StringUtils.isNotBlank(app_version)) {
            String property = "包含";
            if(app_version.startsWith(">=")) {
                property = "大于等于";
            } else if(app_version.startsWith("<=")) {
                property = "小于等于";
            } else if(app_version.startsWith("!")) {
                property = "排除";
            }
            this.directionList.add(new SdkStrategyDirectionReveal("App版本", property, DataStringUtils.stringExplodeList(app_version, ",")));
        }

        // 地域定向
        if(StringUtils.isNotBlank(location_list)) {
            if(location_list.startsWith("!")) {
                this.directionList.add(new SdkStrategyDirectionReveal("地域", "排除",
                        LocationUtils.locationIdList2Name(DataStringUtils.stringExplodeList(location_list.replace("!", ""), ","))));
            } else {
                this.directionList.add(new SdkStrategyDirectionReveal("地域", "包含",
                        LocationUtils.locationIdList2Name(DataStringUtils.stringExplodeList(location_list, ","))));
            }
        }

        // 制造商定向
        if(StringUtils.isNotBlank(make_list)) {
            if(make_list.startsWith("!")) {
                this.directionList.add(new SdkStrategyDirectionReveal("制造商", "排除", DirectionUtils.makeList2Name(DataStringUtils.stringExplodeList(make_list.replace("!", ""), ","))));
            } else {
                this.directionList.add(new SdkStrategyDirectionReveal("制造商", "包含", DirectionUtils.makeList2Name(DataStringUtils.stringExplodeList(make_list, ","))));
            }
        }

        // 操作系统版本定向
        if(StringUtils.isNotBlank(osv_list)) {
            if(osv_list.startsWith("!")) {
                this.directionList.add(new SdkStrategyDirectionReveal("操作系统版本", "排除", DataStringUtils.stringExplodeList(osv_list.replace("!", ""), ",")));
            } else {
                this.directionList.add(new SdkStrategyDirectionReveal("操作系统版本", "包含", DataStringUtils.stringExplodeList(osv_list, ",")));
            }
        }

        // 自定义定向
        if(StringUtils.isNotBlank(customer_key)) {
            this.directionList.add(new SdkStrategyDirectionReveal("自定义定向", "定向key", DataStringUtils.stringExplodeList(customer_key, ",")));
        }

        // 设备包定向
        if(null != device_package_operator && StringUtils.isNotBlank(device_package_list)) {
            String property = 0 == device_package_operator ? "包含" : "排除";
            this.directionList.add(new SdkStrategyDirectionReveal("设备包", property, DataStringUtils.stringExplodeList(device_package_list, ",")));
        }
    }
}
