package com.easyads.management.distribution.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChannelTrafficDirectionReveal {
    private String name;
    private String property;
    private List<String> value;
}
