package com.easyads.management.adspot.service;

import com.easyads.management.common.SystemCode;
import com.easyads.component.enums.SystemCodeEnum;
import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.SystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdspotPropertyService {
    @Autowired
    private SystemMapper systemMapper;

    public Map<String, Object> getAdspotType() throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();

        List<SystemCode> codeList = systemMapper.getSystemCodeList(SystemCodeEnum.ADSPOT_TYPE.getValue());

        resultMap.put("code-list", codeList);

        return resultMap;
    }
}
