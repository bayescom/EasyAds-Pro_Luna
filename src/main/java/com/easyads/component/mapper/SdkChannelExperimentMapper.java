package com.easyads.component.mapper;

import com.easyads.management.distribution.strategy.model.exp.SdkExperiment;
import org.springframework.stereotype.Component;

@Component
public interface SdkChannelExperimentMapper {

    SdkExperiment getSdkExperimentByName(String expName);

    int createSdkExperiment(SdkExperiment sdkExperiment, Integer adspotId, int expType);

    int updateSdkExperiment(SdkExperiment sdkExperiment);

    int closeSdkExperiment(Integer expId);
}
