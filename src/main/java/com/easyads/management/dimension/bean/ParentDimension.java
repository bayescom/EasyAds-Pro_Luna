package com.easyads.management.dimension.bean;

import lombok.Data;

import java.util.List;

@Data
public class ParentDimension {
    private String name;
    private String value;
    private List<ChildDimension> children;
}
