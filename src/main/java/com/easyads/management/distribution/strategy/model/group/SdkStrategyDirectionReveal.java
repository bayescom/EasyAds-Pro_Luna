package com.easyads.management.distribution.strategy.model.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SdkStrategyDirectionReveal {
    private String name;
    private String property;
    private List<String> value;

    public SdkStrategyDirectionReveal(String name) {
        this.name = name;
        this.property = StringUtils.EMPTY;
        this.value = new ArrayList<>();
    }
}
