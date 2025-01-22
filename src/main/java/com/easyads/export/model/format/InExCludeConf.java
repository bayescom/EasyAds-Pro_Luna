package com.easyads.export.model.format;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
public class InExCludeConf {
    String larger = StringUtils.EMPTY;
    String smaller = StringUtils.EMPTY;
    List<String> include;
    List<String> exclude;

    public InExCludeConf(List<String> include, List<String> exclude) {
        this.include = include;
        this.exclude = exclude;
    }

    public InExCludeConf(String larger, String smaller, List<String> include, List<String> exclude) {
        this.larger = larger;
        this.smaller = smaller;
        this.include = include;
        this.exclude = exclude;
    }
}
