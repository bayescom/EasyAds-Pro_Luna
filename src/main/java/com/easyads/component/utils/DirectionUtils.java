package com.easyads.component.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DirectionUtils {
    public static List<String> makeList2Name(List<String> valueList) {
        if(CollectionUtils.isEmpty(valueList)) {
            return new ArrayList<>();
        }

        Set<String> nameList = new LinkedHashSet<>();
        for(String value : valueList) {
            String name = SystemUtils.getMakeNameById(value);
            if(StringUtils.isNotBlank(name)) {
                nameList.add(name);
            }
        }

        return new ArrayList<>(nameList);
    }

    public static List<String> locationIdList2Name(List<String> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }

        Set<String> nameList = new LinkedHashSet<>();
        for(String id : idList) {
            String name = SystemUtils.getLocationNameById(id);
            if(StringUtils.isNotBlank(name)) {
                nameList.add(name);
            }
        }

        return new ArrayList<>(nameList);
    }

}
