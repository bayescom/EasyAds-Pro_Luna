package com.easyads.export.model.format.custom_adn;

import com.easyads.export.model.origin.SdkCustomAdnOrigin;
import lombok.Data;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class AppSdkCustomAdnConf {

    private List<SdkCustomAdn> custom_adn_list;
    private String version;

    public AppSdkCustomAdnConf(Integer platform,
                               Set<Integer> customAdnList,
                               Map<Integer, Map<Integer, SdkCustomAdnOrigin>> sdkCustomAdnOriginMap) {
        this.custom_adn_list = new ArrayList<>();
        for (Integer customAdnId : customAdnList) {
            Map<Integer, SdkCustomAdnOrigin> platformOriginMap = sdkCustomAdnOriginMap.get(customAdnId);
            if (null == platformOriginMap) {
                continue;
            }
            SdkCustomAdnOrigin scao = platformOriginMap.get(platform);
            if (null != scao) {
                this.custom_adn_list.add(new SdkCustomAdn(scao));
            }
        }

        this.version = DigestUtils.md5DigestAsHex(this.custom_adn_list.toString().getBytes());
    }
}
