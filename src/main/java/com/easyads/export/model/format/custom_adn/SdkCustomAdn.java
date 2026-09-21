package com.easyads.export.model.format.custom_adn;

import com.easyads.component.utils.JsonUtils;
import com.easyads.export.model.origin.SdkCustomAdnOrigin;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SdkCustomAdn {
    private Integer id;
    private String name;
    private String custom_adn_module;
    private String custom_config_adapter;
    private String custom_splash_adapter;
    private String custom_banner_adapter;
    private String custom_interstitial_adapter;
    private String custom_rewardvideo_adapter;
    private String custom_nativeexpress_adapter;
    private String custom_renderfeed_adapter;

    public SdkCustomAdn(SdkCustomAdnOrigin scao) {
        this.id = scao.getId();
        this.name = scao.getName();
        JsonNode configJson = StringUtils.isNotBlank(scao.getConfig())
                ? JsonUtils.getJsonNode(scao.getConfig())
                : JsonUtils.getJsonNode("{}");
        this.custom_adn_module = configJson.path("module_name").asText(StringUtils.EMPTY);
        this.custom_config_adapter = configJson.path("init").asText(StringUtils.EMPTY);
        this.custom_splash_adapter = configJson.path("coopen").asText(StringUtils.EMPTY);
        this.custom_banner_adapter = configJson.path("banner").asText(StringUtils.EMPTY);
        this.custom_interstitial_adapter = configJson.path("interstitial").asText(StringUtils.EMPTY);
        this.custom_rewardvideo_adapter = configJson.path("reward").asText(StringUtils.EMPTY);
        this.custom_nativeexpress_adapter = configJson.path("template_feeds").asText(StringUtils.EMPTY);
        this.custom_renderfeed_adapter = configJson.path("custom_feeds").asText(StringUtils.EMPTY);
    }
}
