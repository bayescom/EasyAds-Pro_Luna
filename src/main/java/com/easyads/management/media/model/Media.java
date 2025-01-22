package com.easyads.management.media.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Media {
    private Long id;
    private Integer platformType;
    private String platformTypeName;
    private String mediaName;
    private String bundleName;
    private Integer adspotCount;
    private Integer status;
}
