package com.easyads.management.distribution.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SdkParams {
    @JsonProperty("app_id")
    private String mediaId;

    @JsonProperty("app_key")
    private String mediaKey;

    @JsonProperty("app_secret")
    private String mediaSecret;

    @JsonProperty("adspot_id")
    private String adspotId;
}
