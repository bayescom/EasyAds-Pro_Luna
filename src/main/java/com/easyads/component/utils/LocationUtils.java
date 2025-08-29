package com.easyads.component.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LocationUtils {
    public static List<String> locationIdList2Name(List<String> idList) {
        if(CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }

        Set<String> nameList = new LinkedHashSet<>();
        for(String id : idList) {
            // TODO AB测试 没有BlinkUtils和对应的表
            String name = "未知";
//            String name = BlinkUtils.provinceCityNameMap.get(id);
            if(StringUtils.isNotBlank(name)) {
                nameList.add(name);
            }
        }

        return new ArrayList<>(nameList);
    }

}
