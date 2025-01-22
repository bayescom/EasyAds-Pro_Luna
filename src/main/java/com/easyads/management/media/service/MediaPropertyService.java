package com.easyads.management.media.service;

import com.easyads.component.enums.SystemCodeEnum;
import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.SystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MediaPropertyService {
    @Autowired
    private SystemMapper systemMapper;

    public Map<String, Object> getPlatformList() throws BadRequestException {
        Map<String, Object> resourceResult = new HashMap<>();

        resourceResult.put("code-list", systemMapper.getSystemCodeList(SystemCodeEnum.MEDIA_OS.getValue()));

        return resourceResult;
    }
}
