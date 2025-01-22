package com.easyads.management.report.model.bean.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MetaAdspotFilterUnit {
    private Integer id; // 前端筛选框为了保证唯一性需要给一个纯数字id
    private String value;
    private String name;
    private Integer channelId;
    @JsonIgnore
    private String channelIdMetaAdspotIdStr; // 因为不同渠道广告源有可能重名，所以用渠道id+广告源id来匹配

    public String getChannelIdMetaAdspotIdStr() {
        return channelId + "_" + value;
    }
}
