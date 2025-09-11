package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import lombok.Data;

@Data
public class KsKeyPair {
    private String accessKey;
    private String securityKey;

    public KsKeyPair(String accessKey, String securityKey) {
        this.accessKey = accessKey;
        this.securityKey = securityKey;
    }
}
