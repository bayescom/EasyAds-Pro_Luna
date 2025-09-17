package com.easyads.management.experiment.exp.service;


import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.SdkExperimentMapper;
import com.easyads.management.experiment.exp.model.bean.SdkExperimentBean;
import com.easyads.management.experiment.exp.model.bean.SdkExperimentFilterUnit;
import com.easyads.management.experiment.exp.model.filter.SdkExperimentFilterParams;
import com.easyads.management.report.model.bean.filter.AdspotFilterUnit;
import com.easyads.management.report.model.bean.filter.MediaFilterUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SdkExperimentService {
    @Autowired
    private SdkExperimentMapper sdkExperimentMapper;

    public Map<String, Object> getSdkExperimentList(Map<String, Object> queryParams) throws Exception {
        SdkExperimentFilterParams filterParams = new SdkExperimentFilterParams(queryParams);
        Map<String, Object> sdkExperimentResult = new HashMap(){{
            put("meta", new HashMap(){{
                put("total", 0);
            }});
        }};

        int sdkExperimentCount = sdkExperimentMapper.getSdkExperimentCount(filterParams);
        List<SdkExperimentBean> sdkExperimentList = sdkExperimentMapper.getSdkExperimentList(filterParams);


        ((Map)sdkExperimentResult.get("meta")).put("total", sdkExperimentCount);

        sdkExperimentResult.put("sdk_experiment_list", sdkExperimentList);
        return sdkExperimentResult;
    }

    public Map<String, Object> getOneSdkExperiment(Long id) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap(){{
            put("sdk_experiment", new ArrayList<>());
        }};
        SdkExperimentBean sdkExperiment = sdkExperimentMapper.getOneSdkExperimentByExpId(id);

        resultMap.put("sdk_experiment", sdkExperiment);
        return resultMap;
    }

    public Object getSdkExperimentFilterList() throws Exception {
        List<SdkExperimentFilterUnit> sdkExperimentList = sdkExperimentMapper.getSdkExperimentFilterList();
        return sdkExperimentList;
    }

    public Object getSdkExperimentFilterMediaList() throws Exception {
        List<MediaFilterUnit> sdkExperimentFilterMediaListList = sdkExperimentMapper.getSdkExperimentFilterMediaList();
        return sdkExperimentFilterMediaListList;
    }

    public Object getSdkExperimentFilterAdspotList(Map<String, Object> queryParams) throws Exception {
        SdkExperimentFilterParams filterParams = new SdkExperimentFilterParams(queryParams);
        List<AdspotFilterUnit> sdkExperimentFilterAdspotList = sdkExperimentMapper.getSdkExperimentFilterAdspotList(filterParams);
        return sdkExperimentFilterAdspotList;
    }
}
