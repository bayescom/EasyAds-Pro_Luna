package com.easyads.management.version.service;

import com.easyads.component.enums.SystemCodeEnum;
import com.easyads.component.mapper.SystemMapper;
import com.easyads.component.mapper.VersionMapper;
import com.easyads.management.version.model.bean.Appver;
import com.easyads.management.version.model.filter.AppverFilterParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppVersionService {

    @Autowired
    private VersionMapper versionMapper;

    public Map<String, Object> getAppverList(Map<String, Object> requestParam) {
        Map<String, Object> appVerMap = new HashMap() {{
            put("meta", new HashMap() {{
                put("total", 0);
            }});
            put("appver-list", new ArrayList<>());
        }};

        AppverFilterParams appverFilterParams = new AppverFilterParams(requestParam);
        int totalCount = versionMapper.getAppverCount(SystemCodeEnum.APPVER_DIRECTION.getValue(), appverFilterParams);
        List<Appver> appverList = versionMapper.getAppverList(SystemCodeEnum.APPVER_DIRECTION.getValue(), appverFilterParams);

        ((Map) appVerMap.get("meta")).put("total", totalCount);
        appVerMap.put("appver-list", appverList);

        return appVerMap;
    }

    public Map<String, Object> getOneAppver(Long id) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("appver", versionMapper.getOneAppver(SystemCodeEnum.APPVER_DIRECTION.getValue(), id));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> addOneAppver(Appver appver) {
        Map<String, Object> resultMap = new HashMap<>();
        versionMapper.createOneAppver(SystemCodeEnum.APPVER_DIRECTION.getValue(), appver);
        resultMap.put("appver", versionMapper.getOneAppver(SystemCodeEnum.APPVER_DIRECTION.getValue(), appver.getId()));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneAppver(Long id, Appver appver) {
        Map<String, Object> resultMap = new HashMap<>();
        versionMapper.updateOneAppver(id, appver);
        resultMap.put("appver", versionMapper.getOneAppver(SystemCodeEnum.APPVER_DIRECTION.getValue(), id));
        return resultMap;
    }

}
