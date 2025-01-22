package com.easyads.export.model.format;

import com.easyads.export.model.origin.SdkGroupStrategyOrigin;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class SdkStrategyTarget {
    private Map<String, InExCludeConf> system_direct;

    public SdkStrategyTarget(SdkGroupStrategyOrigin sgso) {
        setSystemDirect(sgso.getApp_version(), sgso.getSdk_version());
    }

    private void setSystemDirect(String app_ver, String sdk_ver) {
        this.system_direct = new HashMap<>();
        this.system_direct.put("app_version", getVersionConf(app_ver));
        this.system_direct.put("sdk_version", getVersionConf(sdk_ver));
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
}
