package com.easyads.management.adn.model.bean;

import com.easyads.component.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class SdkAdnReportApi {
    protected Integer id;
    protected String name;
    @JsonIgnore
    protected String params;
    protected Map<String, String> channelParams = new HashMap<>();
    protected int status;

    public Map<String, String> getChannelParams() {
        if(StringUtils.isNotBlank(this.params)) {
            this.channelParams = JsonUtils.convertJsonToObject(this.params, Map.class);
        }
        return channelParams;
    }

    public boolean needCreate() {
        // 如果一个参数id为空，但是有参数名，并且
        return null == this.id && StringUtils.isNotBlank(this.name);
    }

    public void completeDbBean() {
        this.params = JsonUtils.toJsonString(this.channelParams);
    }
}
