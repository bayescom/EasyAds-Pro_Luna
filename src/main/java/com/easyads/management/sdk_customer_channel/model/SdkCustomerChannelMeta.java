package com.easyads.management.sdk_customer_channel.model;

import com.easyads.management.adn.model.bean.ParamMeta;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SdkCustomerChannelMeta {
    private Integer id;
    private String name;
    private Integer metaAppId;
    private String metaAppIdName;
    private Integer metaAppIdRequired;
    private Integer metaAppKey;
    private String metaAppKeyName;
    private Integer metaAppKeyRequired;
    private Integer metaAdspotId;
    private String metaAdspotIdName;
    private Integer metaAdspotIdRequired;

    public List<ParamMeta> toAdnParamsMeta() {
        List<ParamMeta> metas = new ArrayList<>();
        addParamMetaIfEnabled(metas, metaAppId, "app_id", metaAppIdName, metaAppIdRequired);
        addParamMetaIfEnabled(metas, metaAppKey, "app_key", metaAppKeyName, metaAppKeyRequired);
        addParamMetaIfEnabled(metas, metaAdspotId, "adspot_id", metaAdspotIdName, metaAdspotIdRequired);
        return metas;
    }

    private void addParamMetaIfEnabled(List<ParamMeta> metas, Integer enabled, String metaKey, String metaName, Integer metaRequired) {
        if (enabled == null || enabled != 1) {
            return;
        }
        ParamMeta meta = new ParamMeta();
        meta.setMetaKey(metaKey);
        meta.setMetaName(metaName);
        meta.setMetaRequired(metaRequired == null ? 0 : metaRequired);
        metas.add(meta);
    }
}
