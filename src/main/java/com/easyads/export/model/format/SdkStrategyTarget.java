package com.easyads.export.model.format;

import com.easyads.export.model.origin.SdkGroupStrategyOrigin;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class SdkStrategyTarget {
    private Map<String, InExCludeConf> system_direct;

    public SdkStrategyTarget(SdkGroupStrategyOrigin sgso) {
        setSystemDirect(sgso);
    }

    private void setSystemDirect(SdkGroupStrategyOrigin sgso) {
        this.system_direct = new HashMap<>();
        this.system_direct.put("app_version", getVersionConf(sgso.getApp_version()));
        this.system_direct.put("sdk_version", getVersionConf(sgso.getSdk_version()));
        this.system_direct.put("location", getInExcludeConf(sgso.getLocation_list()));
        this.system_direct.put("make", getInExcludeConf(sgso.getMake_list()));
        this.system_direct.put("osv", getInExcludeConf(sgso.getOsv_list()));
    }

    private InExCludeConf getVersionConf(String version) {
        if(StringUtils.isBlank(version)) {
            return null;
        }

        if (version.startsWith(">=")) {
            return new InExCludeConf(version.replace(">=", ""), StringUtils.EMPTY, new ArrayList<>(), new ArrayList<>());
        } else if (version.startsWith("<=")) {
            return new InExCludeConf(StringUtils.EMPTY, version.replace("<=", ""), new ArrayList<>(), new ArrayList<>());
        } else if (version.startsWith("!")) {
            return new InExCludeConf(new ArrayList<>(), Arrays.asList(version.replace("!", "").split(",")));
        } else {
            return new InExCludeConf(Arrays.asList(version.split(",")), new ArrayList<>());
        }
    }

    private InExCludeConf getInExcludeConf(String directInfo) {
        if (StringUtils.isBlank(directInfo)) {
            return null;
        }

        if (directInfo.startsWith("!")) {
            return new InExCludeConf(new ArrayList<>(), Arrays.asList(directInfo.replace("!", "").split(",")));
        } else {
            return new InExCludeConf(Arrays.asList(directInfo.split(",")), new ArrayList<>());
        }
    }
}
