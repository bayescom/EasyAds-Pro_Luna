package com.easyads.management.dimension.service;

import com.easyads.component.enums.SystemCodeEnum;
import com.easyads.component.mapper.SystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DimensionService {

    @Autowired
    private SystemMapper systemMapper;

    public Map<String, Object> getLocationInfo() {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("dimension-list", systemMapper.getParentDimensionList(SystemCodeEnum.LOCATION_DIRECTION.getValue()));

        return resultMap;
    }


    public Map<String, Object> getMakeInfo() {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("dimension-list", systemMapper.getDimensionList(SystemCodeEnum.MAKE_DIRECTION.getValue()));

        return resultMap;
    }

    public Map<String, Object> getOsvInfo() {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("dimension-list", systemMapper.getParentDimensionList(SystemCodeEnum.OSV_DIRECTION.getValue()));

        return resultMap;
    }

    public Map<String, Object> getAppverInfo(String mediaId) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("dimension-list", systemMapper.getDimensionListByFilter(SystemCodeEnum.APPVER_DIRECTION.getValue(), mediaId));

        return resultMap;
    }

    public Map<String, Object> getSdkverInfo() {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("dimension-list", systemMapper.getDimensionList(SystemCodeEnum.SDKVER_DIRECTION.getValue()));

        return resultMap;
    }
}
