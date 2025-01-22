package com.easyads.component.mapper;

import com.easyads.management.adspot.model.Adspot;
import com.easyads.management.adspot.model.AdspotFilterParams;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdspotMapper {
    // 获取广告列表信息
    int getAdspotCount(AdspotFilterParams adspotFilterParams);
    List<Adspot> getAdspotList(AdspotFilterParams adspotFilterParams);

    // 广告位的增删改查
    int createOneAdspot(Adspot adspot);
    Adspot getOneAdspot(long adspotId);
    int updateOneAdspot(long adspotId, Adspot adspot);
    int deleteOneAdspot(long adspotId);
}