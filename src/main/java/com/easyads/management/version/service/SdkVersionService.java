package com.easyads.management.version.service;

import com.easyads.component.enums.SystemCodeEnum;

import com.easyads.component.mapper.VersionMapper;
import com.easyads.management.version.model.bean.Sdkver;
import com.easyads.management.version.model.filter.SdkverFilterParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SdkVersionService {

    @Autowired
    private VersionMapper versionMapper;

    public Map<String, Object> getSdkverList(Map<String, Object> requestParam) {
        Map<String, Object> appVerMap = new HashMap() {{
            put("meta", new HashMap() {{
                put("total", 0);
            }});
            put("sdkver-list", new ArrayList<>());
        }};

        SdkverFilterParams sdkverFilterParams = new SdkverFilterParams(requestParam);
        int totalCount = versionMapper.getSdkverCount(SystemCodeEnum.SDKVER_DIRECTION.getValue(), sdkverFilterParams);
        List<Sdkver> sdkverList = versionMapper.getSdkverList(SystemCodeEnum.SDKVER_DIRECTION.getValue(), sdkverFilterParams);

        ((Map) appVerMap.get("meta")).put("total", totalCount);
        appVerMap.put("sdkver-list", sdkverList);

        return appVerMap;
    }

    public Map<String, Object> getOneSdkver(Long id) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sdkver", versionMapper.getOneSdkver(SystemCodeEnum.SDKVER_DIRECTION.getValue(), id));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> addOneSdkver(Sdkver sdkver) {
        Map<String, Object> resultMap = new HashMap<>();
        versionMapper.createOneSdkver(SystemCodeEnum.SDKVER_DIRECTION.getValue(), sdkver);
        resultMap.put("sdkver", versionMapper.getOneSdkver(SystemCodeEnum.SDKVER_DIRECTION.getValue(), sdkver.getId()));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneSdkver(Long id, Sdkver sdkver) {
        Map<String, Object> resultMap = new HashMap<>();
        versionMapper.updateOneSdkver(id, sdkver);
        resultMap.put("sdkver", versionMapper.getOneSdkver(SystemCodeEnum.SDKVER_DIRECTION.getValue(), id));
        return resultMap;
    }
}
