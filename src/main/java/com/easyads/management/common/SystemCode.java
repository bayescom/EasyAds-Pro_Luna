package com.easyads.management.common;

import lombok.Data;


@Data
public class SystemCode {
    private int id;
    private String value;
    private String name;
    private String extension;
    private String parent_value;
}
