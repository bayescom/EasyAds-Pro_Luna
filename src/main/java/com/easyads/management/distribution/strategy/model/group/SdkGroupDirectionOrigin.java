package com.easyads.management.distribution.strategy.model.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SdkGroupDirectionOrigin {
    private String appVersion;
    private String sdkVersion;
    private String locationList;
    private String makeList;
    private String osvList;

    @Override
    public String toString() {
        List<String> output = new ArrayList<>();

        if(StringUtils.isNotBlank(appVersion)) {
            if(appVersion.startsWith("!")) {
                output.add(String.format("app版本定向 = [排除] %s", appVersion.substring(1)));
            } else if(appVersion.startsWith("<=") || appVersion.startsWith(">=")) {
                output.add(String.format("app版本定向 %s", appVersion));
            } else {
                output.add(String.format("app版本定向 = [包含] %s", appVersion));
            }
        }

        if(StringUtils.isNotBlank(sdkVersion)) {
            if(sdkVersion.startsWith("!")) {
                output.add(String.format("sdk版本定向 = [排除] %s", sdkVersion.substring(1)));
            } else if(sdkVersion.startsWith("<=") || sdkVersion.startsWith(">=")) {
                output.add(String.format("sdk版本定向 %s", sdkVersion));
            } else {
                output.add(String.format("sdk版本定向 = [包含] %s", sdkVersion));
            }
        }

        if(StringUtils.isNotBlank(locationList)) {
            if(locationList.startsWith("!")) {
                output.add(String.format("地域定向 = [排除] %s", locationList.substring(1)));
            } else {
                output.add(String.format("地域定向 = [包含] %s", locationList));
            }
        }

        if(StringUtils.isNotBlank(makeList)) {
            if(makeList.startsWith("!")) {
                output.add(String.format("机型定向 = [排除] %s", makeList.substring(1)));
            } else {
                output.add(String.format("机型定向 = [包含] %s", makeList));
            }
        }

        if(StringUtils.isNotBlank(osvList)) {
            if(osvList.startsWith("!")) {
                output.add(String.format("操作系统版本定向 = [排除] %s", osvList.substring(1)));
            } else {
                output.add(String.format("操作系统版本定向 = [包含] %s", osvList));
            }
        }

        return String.format("{%s}", StringUtils.join(output, ","));
    }
}
